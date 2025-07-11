package gem.training_spring.base_app.controller;

import gem.training_spring.base_app.dto.LoginDTO;
import gem.training_spring.base_app.dto.ResLoginDTO;
import gem.training_spring.base_app.entity.User;
import gem.training_spring.base_app.service.UserService;
import gem.training_spring.base_app.util.SecurityUtil;
import gem.training_spring.base_app.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    @Value("${bxt.jwt.refresh-token-validity-in-seconds}")
    private long refeshTokenExpiration;

    @PostMapping("/login")
    @ApiMessage("user login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        ResLoginDTO res = new ResLoginDTO();
        User currentUser = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());
        res.setUserLogin(userLogin);
        String access_token = this.securityUtil.createAccessToken(authentication, res.getUserLogin());
        res.setAccessToken(access_token);
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refeshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(res);
    }

    @PostMapping("/refresh-token")
    @ApiMessage("refresh acccess token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refresh_token) {
        if (refresh_token == null || refresh_token.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh Token is missing !");
        }
        Jwt decodedToken;
        try {
            decodedToken = securityUtil.decodeJwt(refresh_token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Refresh Token !");
        }
        String username = decodedToken.getSubject();
        if (username == null) {
            return ResponseEntity.status(401).body("Invalid Refresh Token !");
        }
        User currentUser = this.userService.handleGetUserByUsername(username);
        if (currentUser == null || !refresh_token.equals(currentUser.getRefreshToken())) {
            return ResponseEntity.status(401).body("Refresh Token dosen't match !");
        }
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        String newAccessToken = securityUtil.createAccessToken(authentication, userLogin);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setUserLogin(userLogin);
        resLoginDTO.setAccessToken(newAccessToken);
        String newRefreshToken = securityUtil.createRefreshToken(currentUser.getUsername(), resLoginDTO);
        this.userService.updateUserToken(newRefreshToken, currentUser.getUsername());
        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refeshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(resLoginDTO);
    }
}
