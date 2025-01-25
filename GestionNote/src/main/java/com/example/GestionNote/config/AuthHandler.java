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
                                            Authentication authentication)
            throws IOException
    {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

            if (roles.contains("ADMIN_USER")) {
                response.sendRedirect("/users/home");
            } else if (roles.contains("ADMIN_NOTES")) {
                response.sendRedirect("/notes/home");
            } else if (roles.contains("ADMIN_SP")) {
                response.sendRedirect("/sp/home");
            } else {
                response.sendRedirect("/auth/login");
            }
    }
}

