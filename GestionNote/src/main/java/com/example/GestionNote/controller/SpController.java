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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/AdminSp")
public class SpController {
    @Autowired
    private UserRepository userRepository;

    // Make the user object accessible to all controller routes
    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                User user = userRepository.findByUsername(username);
                model.addAttribute("user", user);
            }
        }
    }

    @RequestMapping("/home")
    public String home() {
        return "AdminSp/home";
    }

    @RequestMapping("/filieres")
    public String filiere() {
        return "AdminSp/filieres";
    }

    @RequestMapping("/classes")
    public String classes() {
        return "AdminSp/classes";
    }

    @RequestMapping("/modules")
    public String modules() {
        return "AdminSp/modules";
    }

    @RequestMapping("/elements")
    public String elements() {
        return "AdminSp/elements";
    }
}
