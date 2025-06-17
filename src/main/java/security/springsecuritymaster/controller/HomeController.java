package security.springsecuritymaster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "dashboard";
    }
    @GetMapping("/user")
    public String user() {
        return "user";
    }
    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
