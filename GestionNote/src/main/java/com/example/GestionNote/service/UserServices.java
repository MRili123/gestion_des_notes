package com.example.GestionNote.service;

import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class UserServices {
    @Autowired
     private UserRepository userRepository ;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public List <User> getAllUsers(){
        return userRepository.findAll();
    }
    public void updateUserEnabled(int  Id, boolean enabled) {
        // Get the user by ID
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + Id));
        user.setEnabled(enabled);  // Update the enabled status
        userRepository.save(user); // Save the updated user back to the DB
    }
    public boolean checkPassword(User user, String oldPassword) {

        return user.getPassword().equals(oldPassword);
    }

    public void save(User user) {
        userRepository.save(user);
    }
    public User getUserById( int id) {
        return userRepository.findById(id).orElse(null) ;
    }
    public User createUser(User userDetails) {
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        return userRepository.save(userDetails);
    }

    public User updateUser(int id, User userDetails) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setEmail(userDetails.getEmail());
            user.setUsername(userDetails.getUsername());
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return userRepository.save(user);
        }
        return null;
    }


}
