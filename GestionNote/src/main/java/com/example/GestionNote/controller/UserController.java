package com.example.GestionNote.controller;


import com.example.GestionNote.model.Role;
import com.example.GestionNote.model.User;
import com.example.GestionNote.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/AdminUser")
public class UserController {

    @Autowired
    private UserServices userServices ;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            @RequestParam("newpassword") String newpassword,
            @RequestParam("confirmpassword") String confirmpassword,

            RedirectAttributes redirectAttributes) {

        // Find user by ID using getUserById
        User user = userServices.getUserById(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/users/list";
        }

        // Validate if the role string is valid
        if (role == null || role.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Role is required.");
            return "redirect:/users/list";
        }

        // Update the user's role, assuming the role passed is a valid string that matches a Role enum
        try {
            Role userRole = Role.valueOf(role);
            user.setRole(userRole);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid role.");
            return "redirect:/users/list";
        }

        // Validate password (if not empty)
        boolean passwordUpdated = false;
        if (!newpassword.isEmpty()) {
            if (!newpassword.equals(confirmpassword)) {
                redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match.");
                return "redirect:/users/list";
            }
            user.setPassword(passwordEncoder.encode(newpassword));  // Assuming you hash the password
            passwordUpdated = true;  // Flag to indicate password was updated
        }

        // Update the enabled status
        user.setEnabled(enabled);

        // Save the updated user
        userServices.save(user);

        // Prepare the success message
        String successMessage = passwordUpdated
                ? "New password '" + newpassword + "' updated successfully."
                : "Updated successfully.";


        redirectAttributes.addFlashAttribute("success", successMessage);


        return "redirect:/users/list";
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
