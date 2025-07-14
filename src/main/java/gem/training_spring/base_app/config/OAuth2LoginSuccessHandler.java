package gem.training_spring.base_app.config;

import gem.training_spring.base_app.dto.ResLoginDTO;
import gem.training_spring.base_app.entity.User;
import gem.training_spring.base_app.repository.UserRepository;
import gem.training_spring.base_app.service.TokenCacheService;
import gem.training_spring.base_app.service.UserService;
import gem.training_spring.base_app.util.SecurityUtil;
import gem.training_spring.base_app.util.enums.RoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    @Value("${bxt.jwt.refresh-token-validity-in-seconds}")
    private long refeshTokenExpiration;
    private final TokenCacheService tokenCacheService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String registerId = oauthToken.getAuthorizedClientRegistrationId().toUpperCase(); // GOOGLE / FACEBOOK
        Authentication tempAuth = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(tempAuth);
        // Kiểm tra DB, nếu chưa có thì tạo user mới
        User user = userService.handleGetUserByUsername(name);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setPassword(passwordEncoder.encode(registerId));
            user.setRole(RoleEnum.USER);
            userRepository.save(user);
        }
        // Tạo access token
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), email, name);
        String accessToken = securityUtil.createAccessToken(tempAuth, userLogin);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setAccessToken(accessToken);
        resLoginDTO.setUserLogin(userLogin);
        String refreshToken = securityUtil.createRefreshToken(email,resLoginDTO);
        System.out.println("refresh token:"+refreshToken);
        System.out.println("access token"+accessToken);
        userService.updateUserToken(refreshToken, name);
        tokenCacheService.saveToken("refresh_token:" + email, refreshToken);
        tokenCacheService.getToken("refresh_token:" + email);
        // Set cookie refresh token
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refeshTokenExpiration)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Redirect về FE kèm access token
        response.sendRedirect("http://localhost:8080/login-success?token=" + accessToken);
    }

}
