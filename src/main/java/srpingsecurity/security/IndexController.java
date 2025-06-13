package srpingsecurity.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequiredArgsConstructor
public class IndexController {

    AuthenticationTrustResolverImpl trustResolver= new AuthenticationTrustResolverImpl();

    @GetMapping("/")
    public String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return trustResolver.isAnonymous(auth) ? "anonymous" : "authenticated";
    }


    @GetMapping("/user")
    public User user(@AuthenticationPrincipal User user) {
        return user;
    }


    @GetMapping("/currentUser")
    public User currentUser(@CurrentUser User user) {
        return user;
    }



    @GetMapping("/currentUsername")
    public String currentUser2(@CurrentUsername String user) {
        return user;
    }

    @GetMapping("/callable")
    public Callable<Authentication> callable() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        System.out.println("securityContext: " + securityContext);
        System.out.println("parent Thread : " + Thread.currentThread().getName());
        return new Callable<Authentication>() {
            @Override
            public Authentication call() {
                SecurityContext securityContext = SecurityContextHolder.getContext();
                System.out.println("securityContext: " + securityContext);
                System.out.println("child Thread : " + Thread.currentThread().getName());
                return securityContext.getAuthentication();
            }
        };
    }
}
