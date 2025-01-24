package com.yash.Vegetabledeliveryonline.controller;

import com.yash.Vegetabledeliveryonline.config.JwtService;
import com.yash.Vegetabledeliveryonline.domain.User;
import com.yash.Vegetabledeliveryonline.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.AuthenticationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }




    @PostMapping(value = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> register(
            @RequestPart(value = "user") User user,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                user.setImage(imageFile.getBytes());
            }

            if (user.getRole().equals(UserService.ROLE_SELLER)) {
                user.setLoginStatus(UserService.LOGIN_STATUS_BLOCKED);
            } else {
                user.setLoginStatus(UserService.LOGIN_STATUS_ACTIVE);
            }

            User registeredUser = userService.register(user);

            // Return JSON response
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "message", "Registration successful",
                            "user", registeredUser
                    ));

        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Username already exists"));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Error processing image file"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            User user = userService.findByLoginName(credentials.get("loginName"));

            // Check if user exists and is not blocked
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials"));
            }

            if (user.getLoginStatus() == UserService.LOGIN_STATUS_BLOCKED) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Account is blocked"));
            }

            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.get("loginName"),
                            credentials.get("password")
                    )
            );

            // Generate Token
            String jwtToken = jwtService.generateToken(user);

            // Create sanitized user object
            Map<String, Object> sanitizedUser = new HashMap<>();


            sanitizedUser.put("name", user.getName());
            sanitizedUser.put("email", user.getEmail());
            sanitizedUser.put("role", user.getRole());
            sanitizedUser.put("userId", user.getUserId());
            // Add other non-sensitive fields as needed

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("user", sanitizedUser);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().body("Logged out successfully");
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        return ResponseEntity.ok().body(Map.of(
                "available", !userService.isUsernameExist(username)
        ));
    }

    @PostMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeStatus(@PathVariable Long userId,
                                          @RequestParam Integer status) {
        try {
            userService.changeLoginStatus(userId, status);
            return ResponseEntity.ok().body("Status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating status");
        }
    }
}