package com.yash.Vegetabledeliveryonline.controller;

import com.yash.Vegetabledeliveryonline.domain.User;
import com.yash.Vegetabledeliveryonline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")

@CrossOrigin

public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/total-users")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> getTotalUsers() {
        try {
            long total = userService.getTotalUsers();
            return ResponseEntity.ok(total);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buyers")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<User>> getBuyerList() {
        try {
            List<User> buyers = userService.getBuyerList();
            return ResponseEntity.ok(buyers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<User>> getSellerList() {
        try {
            // Add logging
            System.out.println("Fetching seller list...");
            List<User> sellers = userService.getSellerList();
            System.out.println("Found " + sellers.size() + " sellers");
            return ResponseEntity.ok(sellers);
        } catch (Exception e) {
            // Add detailed error logging
            System.err.println("Error fetching sellers: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/change-status/{userId}")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> changeUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer loginStatus) {
        try {
            userService.changeLoginStatus(userId, loginStatus);
            return ResponseEntity.ok()
                    .body(Map.of("message", "User status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user status: " + e.getMessage()));
        }
    }

    @GetMapping("/user-image/{userId}")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);

            if (user == null || user.getImage() == null || user.getImage().length == 0) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(user.getImage().length);
            headers.setCacheControl("public, max-age=86400"); // Cache for 24 hours

            return new ResponseEntity<>(user.getImage(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

