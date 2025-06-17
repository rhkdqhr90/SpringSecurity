package security.springsecuritymaster.security.configs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import security.springsecuritymaster.security.details.FormWebAuthenticationDetailsSource;
import security.springsecuritymaster.security.handler.FromAuthenticationSuccessHandler;
import security.springsecuritymaster.security.provider.FormAuthenticationProvider;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final FormAuthenticationProvider authenticationProvider;
    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> formWebAuthenticationDetailsSource;
    private final FromAuthenticationSuccessHandler fromAuthenticationSuccessHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->auth
                .requestMatchers("/css/**","/images/**","/js/**","/favicon.*/","/*/icon-*").permitAll()
                .requestMatchers("/","/signup").permitAll()
                .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").permitAll()
                        .authenticationDetailsSource(formWebAuthenticationDetailsSource)
                        .successHandler(fromAuthenticationSuccessHandler))
                .authenticationProvider(authenticationProvider);

        return http.build();
    }




}
