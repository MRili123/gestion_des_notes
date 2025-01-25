package com.example.GestionNote.config;
import com.example.GestionNote.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
    @Autowired
    private AuthHandler authHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
<<<<<<< HEAD
                        .requestMatchers("/users/**").hasAuthority("ROLE_ADMIN_USER")
=======
                        .requestMatchers("/users/**").hasAuthority("ADMIN_USER")
                        .requestMatchers("/notes/**").hasAuthority("ADMIN_NOTES")
                        .requestMatchers("/sp/**").hasAuthority("ADMIN_SP")
>>>>>>> df69e0dc75fa16ce88a861e6568a29c337a53ca3
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authHandler)
                        .permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
