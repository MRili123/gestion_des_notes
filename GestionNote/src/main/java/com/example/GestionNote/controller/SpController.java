package com.example.GestionNote.controller;

import com.example.GestionNote.DTO.CustomUserDetails;
import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.UserRepository;
import com.example.GestionNote.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/AdminSp")
public class SpController {
    @Autowired
    private UserRepository userRepository;
    @RequestMapping("/home")
    public String home(Model model) {
// Get the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userRepository.findByUsername(username);

                // Add the username to the model
                model.addAttribute("user", user);
            }
        }
        return "AdminSp/home";
    }
}
