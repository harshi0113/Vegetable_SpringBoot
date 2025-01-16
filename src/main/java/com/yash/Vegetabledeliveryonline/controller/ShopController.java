package com.yash.Vegetabledeliveryonline.controller;


import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestParam Long userId) {
        Map<String, Object> dashboard = new HashMap<>();

        List<Shop> shops = shopService.findByUserId(userId);
        boolean hasExistingShop = shopService.hasExistingShop(userId);

        dashboard.put("shops", shops);
        dashboard.put("hasExistingShop", hasExistingShop);

        return ResponseEntity.ok(dashboard);
    }


    @PostMapping
    public ResponseEntity<?> createShop(@RequestBody Shop shop) {
        if (shopService.hasExistingShop(shop.getUserId())) {
            return ResponseEntity.badRequest().body("User already has a shop");
        }
        return ResponseEntity.ok(shopService.saveShop(shop));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(@PathVariable Long id, @RequestBody Shop shop) {
        shop.setId(id);
        return ResponseEntity.ok(shopService.updateShop(shop));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShop(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserShops(@PathVariable Long userId) {
        return ResponseEntity.ok(shopService.findByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShop(@PathVariable Long id) {
        shopService.deleteShop(id);
        return ResponseEntity.ok().build();
    }
}