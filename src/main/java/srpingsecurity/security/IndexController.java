package srpingsecurity.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexController {

    @GetMapping("/")
    public Authentication index(Authentication authentication) {
        return authentication;
    }


    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/user/{name}")
    public String userName(@PathVariable(value = "name") String name) {
        return name;
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/custom/db")
    public String custom() {
        return "custom";
    }
}
