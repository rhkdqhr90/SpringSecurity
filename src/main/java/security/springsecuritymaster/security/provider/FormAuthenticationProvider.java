package security.springsecuritymaster.security.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import security.springsecuritymaster.domain.dto.AccountContext;
import security.springsecuritymaster.security.details.FormAuthenticationDetails;
import security.springsecuritymaster.security.exception.SecretException;

@Component("authenticationProvider")
@RequiredArgsConstructor
public class FormAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginId = authentication.getName();
        String password = (String) authentication.getCredentials();

        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(loginId);
        if (!passwordEncoder.matches(password, accountContext.getPassword())) {
            throw new BadCredentialsException("Bad Credentials");
        }
        String secretKey = ((FormAuthenticationDetails) authentication.getDetails()).getSecretKey();
        if(secretKey == null || !
                secretKey.equals("secret")){
            throw new SecretException("Invalid Secret Key");
        }
        return new UsernamePasswordAuthenticationToken(accountContext.getAccountDto(), null, accountContext.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
