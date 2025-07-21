package com.tossupflash.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse register(String username, String email, String password) {
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            return new AuthResponse(null, null, "Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            return new AuthResponse(null, null, "Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId());
        
        return new AuthResponse(token, savedUser.getUsername(), null);
    }
    
    public AuthResponse login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return new AuthResponse(null, null, "Invalid username or password");
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new AuthResponse(null, null, "Invalid username or password");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(token, user.getUsername(), null);
    }
    
    public static class AuthResponse {
        private String token;
        private String username;
        private String error;
        
        public AuthResponse(String token, String username, String error) {
            this.token = token;
            this.username = username;
            this.error = error;
        }
        
        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public boolean isSuccess() { return error == null; }
    }
} 