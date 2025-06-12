package srpingsecurity.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final String REQUIRED_ROLE = "ROLE_SECURE";

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        Authentication auth = authentication.get();

        if(auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken ){
            return new AuthorizationDecision(false);
    }
        boolean hasRequiredRole = auth.getAuthorities().stream().anyMatch(a -> REQUIRED_ROLE.equals(a.getAuthority()));
        return new AuthorizationDecision(hasRequiredRole);
    }
}
