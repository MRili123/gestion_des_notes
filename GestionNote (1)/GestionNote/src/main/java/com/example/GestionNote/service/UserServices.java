package com.example.GestionNote.service;

import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class UserServices {
    @Autowired
     private UserRepository userRepository ;
    public List <User> getAllUsers(){
        return userRepository.findAll();
    }
    public User getUserById( int id) {
        return userRepository.findById(id).orElse(null) ;
    }
    public User createUser( User userDetails){
       return  userRepository.save(userDetails) ;
    }
    public User updateUser (int id, User userDetails){
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setEmail(userDetails.getEmail());
            user.setFullname(userDetails.getFullname());
            user.setUsername(userDetails.getUsername());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        }
        return null;
    }
    public void deleteUser(int id ){
        userRepository.deleteById(id);
    }



}
