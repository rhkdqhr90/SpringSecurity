package srpingsecurity.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexController {
    private final SessionInfoService sessionInfoService;
    @Autowired
    SecurityContextService securityContextService;

    @GetMapping("/sessionInfo")
    public String sessionInfo() {
        sessionInfoService.sessionInfo();
        return "sessionInfo";
    }

    @GetMapping("/")
    public Authentication index(Authentication authentication ) {
        securityContextService.setSecurityContext();
        return authentication;
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


    @GetMapping("/invalidSessionUrl")
    public String invalidSessionUrl() {
        return "invalidSessionUrl";

    }

    @GetMapping("/expiredUrl")
    public String expiredUrl() {
        return "expiredUrl";

    }


}
