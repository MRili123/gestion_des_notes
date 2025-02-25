package com.example.GestionNote.controller;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Controller
public class AuthController implements ErrorController {

    @GetMapping("/auth/login")
    public String login(Authentication authentication) {
        if(authentication != null) {
            return "redirect:/";
        }
        return "auth/login";
    }
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("error", "You are disabled and cannot perform this action.");
        return "/403";
    }

    @GetMapping("/")
    public String rootRedirect(Authentication authentication) {
        if (authentication != null) {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (roles.contains("ADMIN_USER")) {
                return "redirect:/AdminUser/home";
            } else if (roles.contains("ADMIN_NOTES")) {
                return "redirect:/AdminNotes/home";
            } else if (roles.contains("ADMIN_SP")) {
                return "redirect:/AdminSp/home";
            }
        }
        return "redirect:/auth/login";
    }


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // You can add custom error handling logic here
        return "/404";
    }



}
