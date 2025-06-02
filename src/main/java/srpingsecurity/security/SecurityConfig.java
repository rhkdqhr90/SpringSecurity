package srpingsecurity.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


import java.io.IOException;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(auth -> auth
                            .requestMatchers("/logoutSuccess").permitAll()
                            .anyRequest().authenticated())
                    .formLogin(Customizer.withDefaults())
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutRequestMatcher((request -> request.getMethod().equals("POST") && request.getRequestURI().equals("/logout")))
                            .logoutSuccessUrl("/logoutSuccess")
                            .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/logoutSuccess"))
                            .deleteCookies("JSESSIONID","remember-me")
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .addLogoutHandler((request, response, authentication) -> {
                                request.getSession().invalidate();
                                SecurityContextHolder.clearContext();

                            })
                            .permitAll()
                    );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{noop}1111")
                .roles("USER")
                .build();
        return  new InMemoryUserDetailsManager(user);
    }
}
