package com.example.GestionNote.controller;


import com.example.GestionNote.model.User;
import com.example.GestionNote.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServices userServices ;

    // Home page
    @GetMapping("/home")
    public String home() {
        return "AdminUser/home";
    }

    // View all users
    @GetMapping("/list")
    public String listUsers(Model model) {
        model.addAttribute("users", userServices.getAllUsers());
        return "AdminUser/UserList";
    }
    @PostMapping("/saveChanges")
    public String saveChanges(@RequestParam boolean enabled, @RequestParam int userId) {
        userServices.updateUserEnabled(userId, enabled);

        return "redirect:/users/list"; // Should redirect to /users/list
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
        return "redirect:/users"; // Redirect to the user list
    }

    // Show form to edit user
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        User user = userServices.getUserById(id);
        model.addAttribute("user", user);
        return "AdminUser/EditUser";
    }


    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable int id, @ModelAttribute User user) {
        userServices.updateUser(id, user);
        return "redirect:/users";
    }


}
