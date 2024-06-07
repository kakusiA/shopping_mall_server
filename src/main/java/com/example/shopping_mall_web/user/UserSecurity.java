package com.example.shopping_mall_web.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    @Autowired
    private UserRepository userRepository;

    public boolean isSeller(Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return currentUser.isSeller();
    }
}
