package com.tourism.tourism_backend.services;


import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user by email
        AppUser appUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Return a UserDetails object (Spring Security's User object)
        return User.builder()
                .username(appUser.getEmail())
                .password(appUser.getPassword())
                .roles(appUser.getRole()) // Set roles from the user entity
                .build();
    }
}


