package com.yash.Vegetabledeliveryonline.controller;

import com.yash.Vegetabledeliveryonline.service.ShopService;
import com.yash.Vegetabledeliveryonline.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShopService shopService;

    @GetMapping("/user-stats")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> getUserDashboardStats(@RequestParam Long userId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Get order statistics
//            stats.put("totalOrders", shopService.getTotalOrdersForUser(userId));
//            stats.put("pendingOrders", shopService.getPendingOrdersForUser(userId));
//            stats.put("completedOrders", shopService.getCompletedOrdersForUser(userId));
//            stats.put("totalSpent", shopService.getTotalSpentByUser(userId));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching dashboard stats: " + e.getMessage());
        }
    }

    @GetMapping("/shops")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> getAllShops() {
        try {
            return ResponseEntity.ok(shopService.getAllShops()); // Call the new method
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching shops: " + e.getMessage());
        }
    }
}