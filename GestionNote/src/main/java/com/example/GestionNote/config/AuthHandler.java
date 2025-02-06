package com.example.GestionNote.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
@Component
public class AuthHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails =
                    (org.springframework.security.core.userdetails.User) principal;

            // Check if the account is locked
            if (userDetails.isAccountNonLocked()) {
                if (roles.contains("ADMIN_USER")) {
                    response.sendRedirect("/AdminUser/home");
                } else if (roles.contains("ADMIN_NOTES")) {
                    response.sendRedirect("/AdminNotes/home");
                } else if (roles.contains("ADMIN_SP")) {
                    response.sendRedirect("/AdminSp/home");
                } else {
                    response.sendRedirect("/auth/login");
                }
            } else {
                // Account is locked
                request.setAttribute("error", "locked");
                request.getRequestDispatcher("/auth/login").forward(request, response);
            }
        } else {
            // Handle invalid user case
            request.setAttribute("error", "invalid");
            request.getRequestDispatcher("/auth/login").forward(request, response);
        }
    }
}
