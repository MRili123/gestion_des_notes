package com.example.GestionNote.service;

import com.example.GestionNote.model.User;
import com.example.GestionNote.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findAllByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Return a custom UserDetails implementation that includes the "enabled" status
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Boolean.TRUE.equals(user.getEnabled()),  // Convert Boolean -> boolean
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                !Boolean.TRUE.equals(user.getLocked()),  // Convert locked -> accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );

    }
}