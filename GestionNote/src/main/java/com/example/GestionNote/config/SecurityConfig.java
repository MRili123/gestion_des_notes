package com.example.GestionNote.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private AuthHandler authHandler;

    @Autowired
    private CustomAuthenticationFailureHandler customFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/theme_assets/**", "/vendor_assets/**")
                        .permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/AdminUser/**").hasAuthority("ADMIN_USER")
                        .requestMatchers("/AdminNotes/**").hasAuthority("ADMIN_NOTES")
                        .requestMatchers("/AdminSp/**").hasAuthority("ADMIN_SP")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .failureHandler(customFailureHandler)
                        .successHandler(authHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                ).exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/access-denied"); // Redirect to a custom access denied page
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}