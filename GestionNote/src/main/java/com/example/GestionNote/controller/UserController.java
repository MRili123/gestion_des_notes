package com.example.GestionNote.controller;


import com.example.GestionNote.model.Professor;
import com.example.GestionNote.model.Role;
import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.ProfessorRepository;
import com.example.GestionNote.repository.UserRepository;
import com.example.GestionNote.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/AdminUser")

public class UserController {
    @Autowired
    private ProfessorRepository professorRepository;

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
            @RequestParam("locked") boolean locked,
            @RequestParam("role") String role,
            @RequestParam(value = "newpassword", required = false) String newpassword,
            @RequestParam(value = "confirmpassword", required = false) String confirmpassword,
            RedirectAttributes redirectAttributes
    ) {


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
        user.setLocked(locked);
        userServices.save(user);

        redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/AdminUser/list";
    }




    // Handle the form submission to add a new user
    @PostMapping("/add")
    public String addUser(
            @RequestParam("inputFirstNameAdd") String firstName,
            @RequestParam("inputLastNameAdd") String lastName,
            @RequestParam("inputUserNameAdd") String username,
            @RequestParam("inputEmailAdd") String email,
            @RequestParam("inputCINAdd") String cni,
            @RequestParam("inputRoleAdd") String role,
            @RequestParam("inputPhoneAdd") String phone,
            @RequestParam("inputPasswordAdd") String password,
            RedirectAttributes redirectAttributes) {

        // Validation: Check if any of the fields are empty
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() ||
                cni.isEmpty() || role.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "All fields are required. Please fill in all fields.");
            return "redirect:/AdminUser/list";
        }

        // Check if the role is valid (example: ADMIN_SP or ADMIN_NOTES)
        if (!role.equals("ADMIN_SP") && !role.equals("ADMIN_NOTES")) {
            redirectAttributes.addFlashAttribute("error", "Invalid role selected.");
            return "redirect:/AdminUser/list";
        }

        // Email validation (check if the email is valid)
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            redirectAttributes.addFlashAttribute("error", "Please enter a valid email address.");
            return "redirect:/AdminUser/list";
        }

        // Phone validation (check if the phone number is valid)
        if (!phone.matches("^\\d{10}$")) {
            redirectAttributes.addFlashAttribute("error", "Please enter a valid phone number.");
            return "redirect:/AdminUser/list";
        }

        // Generate a unique username
        String uniqueUsername = generateUniqueUsername(username);

        // Check if the email already exists
        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already exists.");
            return "redirect:/AdminUser/list";
        }
        String encodedPassword = passwordEncoder.encode(password);
        // If all fields are valid, proceed with saving the user
        try {

            User newUser = new User(firstName, lastName, uniqueUsername, email, cni, role, phone, encodedPassword);
            newUser.setEnabled(true);
            newUser.setLocked(false);
            newUser.setCreatedAt(LocalDateTime.now());
            userServices.save(newUser);

            redirectAttributes.addFlashAttribute("success", "User added successfully with username: " + uniqueUsername);
            return "redirect:/AdminUser/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while saving the user. Please try again.");
            return "redirect:/AdminUser/list";
        }
    }

    // Method to generate a unique username
    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int count = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + "(" + count + ")";
            count++;
        }

        return username;
    }

    @RequestMapping("/professors")
    public String professors(Model model) {
        List<Professor> professors = professorRepository.findByDeleted(false);
        model.addAttribute("professors", professors);
        return "AdminUser/ProfsList";
    }

}








