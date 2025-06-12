package srpingsecurity.security;




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.ArrayList;
import java.util.List;


@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/user").hasRole("USER")
//                        .requestMatchers("/db").access(new WebExpressionAuthorizationManager("hasRole('DB')"))
//                        .requestMatchers("/admin").hasAnyAuthority("ADMIN")
//                        .requestMatchers("/secure").access(new CustomAuthorizationManager())

                        .anyRequest().access(authorizationManager(null )))
                .formLogin(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

//    @Bean
//    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
//        return new GrantedAuthorityDefaults("MYPREFIX_");
//    }


    @Bean
    public AuthorizationManager<RequestAuthorizationContext> authorizationManager(HandlerMappingIntrospector introspector) {
        List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings = new ArrayList<>();

        RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> requestMatcherEntry1 =
                new RequestMatcherEntry<>(new MvcRequestMatcher(introspector,"/user"), AuthorityAuthorizationManager.hasAnyAuthority("USER"))
                ;
        RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> requestMatcherEntry2 =
                new RequestMatcherEntry<>(new MvcRequestMatcher(introspector,"/db"), AuthorityAuthorizationManager.hasAnyAuthority("DB"))
                ;
        RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> requestMatcherEntry3 =
                new RequestMatcherEntry<>(new MvcRequestMatcher(introspector,"/admin"), AuthorityAuthorizationManager.hasAnyAuthority("ADMIN"))
                ;
        RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> requestMatcherEntry4 =
                new RequestMatcherEntry<>(AnyRequestMatcher.INSTANCE, new AuthenticatedAuthorizationManager<>());
        mappings.add(requestMatcherEntry1);
        mappings.add(requestMatcherEntry2);
        mappings.add(requestMatcherEntry3);
        mappings.add(requestMatcherEntry4);

        return new CustomRequestMatcherDelegatingAuthorizationManager(mappings);

    }





    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user").password("{noop}1111").authorities("USER").build();
        UserDetails manager = User.withUsername("db").password("{noop}1111").roles("DB").build();
        UserDetails admin = User.withUsername("admin").password("{noop}1111").roles("ADMIN","SECURE").build();
        return  new InMemoryUserDetailsManager(user,admin,manager);
    }
}
