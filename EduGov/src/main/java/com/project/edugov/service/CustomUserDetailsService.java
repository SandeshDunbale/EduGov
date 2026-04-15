package com.project.edugov.service;

import com.project.edugov.model.Status;
import com.project.edugov.model.User;
import com.project.edugov.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch your custom user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Determine if the user is allowed to log in based on their status
        boolean isEnabled = (user.getStatus() == Status.APPROVE || user.getStatus() == Status.ACTIVE);

        // Map it to Spring Security's UserDetails object using the fully detailed constructor.
        // We prefix the role with "ROLE_" as this is standard Spring Security convention.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                isEnabled, // enabled: strictly tied to APPROVE or ACTIVE status
                true,      // accountNonExpired
                true,      // credentialsNonExpired
                true,      // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}