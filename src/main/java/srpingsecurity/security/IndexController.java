package srpingsecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Autowired
    SecurityContextService securityContextService;

    @GetMapping("/")
    public String index( ) {
        securityContextService.setSecurityContext();
        return "index";
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

    @GetMapping("/logoutSuccess")
    public String logoutSuccess() {
        return "로그아웃 성공";

    }

}
