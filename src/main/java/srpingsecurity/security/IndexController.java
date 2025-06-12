package srpingsecurity.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final DataSource dataSource;

    @GetMapping("/")
    public Authentication index(Authentication authentication) {
        return authentication;
    }


    @GetMapping("/user")
    public String user() {
        return dataSource.getUser();
    }

    @GetMapping("/db")
    public Account oauth(@RequestParam String name) {
        return dataSource.getOwner(name);
    }

    @GetMapping("/secure")
    public String secure() {
        return "secure";
    }
}
