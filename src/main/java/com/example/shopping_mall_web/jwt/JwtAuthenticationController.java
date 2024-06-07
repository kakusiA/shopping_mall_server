package com.example.shopping_mall_web.jwt;

import com.example.shopping_mall_web.user.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/exp")
    public ResponseEntity<?> getTokenExpiration(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Date expiration = jwtUtil.extractExpiration(token);
        return ResponseEntity.ok(expiration);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDto authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getUser().getName(), userDetails.getUser().getAddress());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("token", jwt);
        tokenResponse.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.get("refreshToken");
        if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid or missing refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        final String newJwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getUser().getName(), userDetails.getUser().getAddress());

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("token", newJwt);

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            System.out.println("Register request received: " + userDto);
            // 디버깅용 추가 로그
            System.out.println("Attempting to register user with email: " + userDto.getEmail());
            User user = userService.registerNewUser(userDto.getName(), userDto.getEmail(), userDto.getPassword(), null); // address를 null로 설정
            System.out.println("User registered successfully: " + user);
            return ResponseEntity.ok(user);
        } catch (IllegalStateException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}