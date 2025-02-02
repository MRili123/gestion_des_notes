package com.example.GestionNote.controller;


import com.example.GestionNote.model.Role;
import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.UserRepository;
import com.example.GestionNote.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/AdminUser")

public class UserController {

    @Autowired
    private UserServices userServices ;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();

                // Handle Optional<User> properly
                Optional<User> userOptional = userRepository.findByUsername(username);
                userOptional.ifPresent(user -> model.addAttribute("user", user));
            }
        }
    }


    // Home page
    @GetMapping("/home")
    public String home() {
        return "AdminUser/home";
    }

    // View all users
    @GetMapping("/list")
    public String listUsers(Model model) {
        // Filter out users with the role ADMIN_USER
        List<User> allUsers = userServices.getAllUsers();
        List<User> filteredUsers = allUsers.stream()
                .filter(user -> !user.getRole().equals("ADMIN_USER"))
                .collect(Collectors.toList());

        model.addAttribute("users", filteredUsers);
        return "AdminUser/UserList";
    }
    @PostMapping("/saveChanges")
    public String saveChanges(
            @RequestParam("userId") int userId,
            @RequestParam("enabled") boolean enabled,
            @RequestParam("role") String role,
            @RequestParam(value = "newpassword", required = false) String newpassword,
            @RequestParam(value = "confirmpassword", required = false) String confirmpassword,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("Received userId: " + userId);
        System.out.println("Received enabled: " + enabled);
        System.out.println("Received role: " + role);
        System.out.println("Received newpassword: " + newpassword);
        System.out.println("Received confirmpassword: " + confirmpassword);

        User user = userServices.getUserById(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/AdminUser/list";
        }

        if (role == null || role.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Role is required.");
            return "redirect:/AdminUser/list";
        }

        try {
            Role userRole = Role.valueOf(role);
            user.setRole(userRole);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid role.");
            return "redirect:/AdminUser/list";
        }

        if (newpassword != null && !newpassword.isEmpty()) {
            if (!newpassword.equals(confirmpassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
                return "redirect:/AdminUser/list";
            }
            user.setPassword(passwordEncoder.encode(newpassword));
        }

        user.setEnabled(enabled);
        userServices.save(user);

        redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/AdminUser/list";
    }




    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "AdminUser/CreatUser"; // maps to create-user.html
    }

    // Handle create user form submission
    @PostMapping("/create")
    public String createUser(@ModelAttribute User user) {
        userServices.createUser(user);
        return "redirect:/users";
    }







}
