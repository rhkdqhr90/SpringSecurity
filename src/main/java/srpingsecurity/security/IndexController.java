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


    @GetMapping("/api/db")
    public String api() {
        return "api";
    }

    @GetMapping("/oauth/db")
    public String oauth() {
        return "oauth";
    }
}
