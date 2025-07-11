package gem.training_spring.base_app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginSuccessController {
    @GetMapping("/login-success")
    @PreAuthorize("permitAll()")
    public String loginSuccess(@RequestParam String token){
        return token;
    }
}
