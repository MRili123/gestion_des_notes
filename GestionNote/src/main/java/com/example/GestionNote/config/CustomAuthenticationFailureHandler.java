package com.example.GestionNote.config;

import com.example.GestionNote.model.User;
import com.example.GestionNote.service.LoginLogService;
import com.example.GestionNote.service.UserServices;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Lazy
    @Autowired
    private LoginLogService loginLogService;
    @Lazy
    @Autowired
    private UserServices userServices;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {

        // Get the user details
        String username = request.getParameter("username");
        Integer userId = null;
        if(username != null) {
            User user = userServices.getUserByUsername(request.getParameter("username"));
            if(user != null) {
                userId = user.getId();
            }
        }



        if (exception instanceof LockedException) {
            // If account is locked
            // Log the login
            loginLogService.save(false, "Account Locked", request.getRemoteAddr(), userId);
            response.sendRedirect("/auth/login?error=locked");
        } else if (exception instanceof BadCredentialsException) {
            // If invalid credentials
            // Log the login
            loginLogService.save(false, "Invalid Credentials", request.getRemoteAddr(), userId);
            response.sendRedirect("/auth/login?error=invalid");
        } else {
            // General error
            response.sendRedirect("/auth/login?error=true");
        }
    }
}
