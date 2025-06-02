package srpingsecurity.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("anonymouse")
    public String anonymouse() {
        return "anonymouse";

    }

    @GetMapping("/authentication")
    public String authentication(Authentication authentication) {
        if(authentication instanceof AnonymousAuthenticationToken){
            return anonymouse();
        }else{
            return "not anonymouse";
        }


    }

        @GetMapping("/anonymouseContext")
    public String anonymouseContext(@CurrentSecurityContext SecurityContext context) {
        return context.getAuthentication().getName();

    }

}
