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

    // View all users
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userServices.getAllUsers());
        return "User/userslist";
    }


    // Show form to create user
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "User/creatUser"; // maps to create-user.html
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
        return "User/editUser"; // maps to edit-user.html
    }

    // Handle edit user form submission
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable int id, @ModelAttribute User user) {
        userServices.updateUser(id, user);
        return "redirect:/users";
    }

    // Delete user
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        userServices.deleteUser(id);
        return "redirect:/users";
    }
}
