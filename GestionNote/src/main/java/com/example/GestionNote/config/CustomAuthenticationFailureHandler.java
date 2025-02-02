package com.example.GestionNote.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {

        if (exception instanceof DisabledException) {
            response.sendRedirect("/auth/login?error=disabled");
        } else if (exception instanceof BadCredentialsException) {
            response.sendRedirect("/auth/login?error=invalid");
        } else {
            response.sendRedirect("/auth/login?error=true");
        }
    }
}