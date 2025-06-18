package security.springsecuritymaster.security.configs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import security.springsecuritymaster.security.details.FormWebAuthenticationDetailsSource;
import security.springsecuritymaster.security.dsl.RestApiDsl;
import security.springsecuritymaster.security.entrypoint.RestAuthenticationEntryPoint;
import security.springsecuritymaster.security.filter.RestAuthenticationFilter;
import security.springsecuritymaster.security.handler.*;
import security.springsecuritymaster.security.provider.FormAuthenticationProvider;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final FormAuthenticationProvider authenticationProvider;
    private final AuthenticationProvider restAuthenticationProvider;
    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> formWebAuthenticationDetailsSource;
    private final FromAuthenticationSuccessHandler successHandler;
    private final FormAuthenticationFailureHandler failureHandler;
    private final RestAuthenticationSuccessHandler restSuccessHandler;
    private final RestAuthenticationFailureHandler restFailureHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->auth
                .requestMatchers("/css/**","/images/**","/js/**","/favicon.*/","/*/icon-*").permitAll()
                .requestMatchers("/","/signup","/login*").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").permitAll()
                        .authenticationDetailsSource(formWebAuthenticationDetailsSource)
                        .successHandler(successHandler)
                        .failureHandler(failureHandler))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exception -> exception.accessDeniedHandler(new FormAccessDeniedHandler("/denied")));



        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder managerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        managerBuilder.authenticationProvider(restAuthenticationProvider);
        AuthenticationManager authenticationManager = managerBuilder.build();
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth ->auth
                        .requestMatchers("/css/**","/images/**","/js/**","/favicon.*/","/*/icon-*").permitAll()
                        .requestMatchers("/api","/api/login").permitAll()
                        .requestMatchers("/api/user").hasRole("USER")
                        .requestMatchers("/api/manager").hasRole("MANAGER")
                        .requestMatchers("/api/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())
              //  .csrf(AbstractHttpConfigurer::disable)
              //  .addFilterBefore(restAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler()))
                 .with(new RestApiDsl<>(),restDsl ->restDsl
                        .restSuccessHandler(restSuccessHandler)
                        .restFailureHandler(restFailureHandler)
                        .loginPage("/api/login")
                        .loginProcessingUrl("/api/login"));

        return http.build();
    }
//
//    private RestAuthenticationFilter restAuthenticationFilter(AuthenticationManager authenticationManager) {
//        RestAuthenticationFilter restAuthenticationFilter = new RestAuthenticationFilter();
//        restAuthenticationFilter.setAuthenticationManager(authenticationManager);
//        restAuthenticationFilter.setAuthenticationSuccessHandler(restSuccessHandler);
//        restAuthenticationFilter.setAuthenticationFailureHandler(restFailureHandler);
//        return restAuthenticationFilter;
//    }


}
