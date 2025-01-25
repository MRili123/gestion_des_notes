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
<<<<<<< HEAD
    @Autowired
    private PasswordEncoder passwordEncoder;

=======
>>>>>>> df69e0dc75fa16ce88a861e6568a29c337a53ca3
    public List <User> getAllUsers(){
        return userRepository.findAll();
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
