package gem.training_spring.base_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@Controller
public class DemoController {
    @GetMapping("/login-success")
    String demo(@RequestParam String token){
        return token;
    }
}
