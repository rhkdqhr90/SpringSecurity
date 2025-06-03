package srpingsecurity.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig2 {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = sharedObject.getOrBuild();
            http.authorizeHttpRequests(auth -> auth
                            .requestMatchers("/").permitAll()
                            .anyRequest().authenticated())
                            .authenticationManager(authenticationManager)
                    .addFilterBefore(customAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    public CustomAuthenticationFilter customAuthenticationFilter(HttpSecurity http) throws Exception {
       List<AuthenticationProvider> list1 = List.of(new DaoAuthenticationProvider());
        ProviderManager parent = new ProviderManager(list1);
        List<AuthenticationProvider> list2 = List.of(new AnonymousAuthenticationProvider("key"),new CustomAuthenticationProvider());
        ProviderManager providerManager = new ProviderManager(list1,parent);

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(http);
        customAuthenticationFilter.setAuthenticationManager(providerManager);
        return customAuthenticationFilter;
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
