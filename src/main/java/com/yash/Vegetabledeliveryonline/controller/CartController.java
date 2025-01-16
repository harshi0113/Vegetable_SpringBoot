//package com.yash.Vegetabledeliveryonline.controller;
//
//import com.yash.Vegetabledeliveryonline.domain.Cart;
//import com.yash.Vegetabledeliveryonline.service.CartService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:4200")
//public class CartController {
//
//    @Autowired
//    private CartService cartService;
//
//    @PostMapping("/user/add-to-cart")
//    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request) {
//        try {
//            Integer userId = (Integer) request.get("userId");
//            Integer menuId = (Integer) request.get("menuId");
//            Integer quantity = (Integer) request.get("quantity");
//            Integer contactId = (Integer) request.get("contactId");
//
//            cartService.addToCart(userId, menuId, quantity, contactId);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/user/cart/{userId}")
//    public ResponseEntity<List<Cart>> getCartItems(@PathVariable Integer userId) {
//        List<Cart> cartItems = cartService.getCartItems(userId);
//        return ResponseEntity.ok(cartItems);
//    }
//
//    @PostMapping("/user/update-cart-item")
//    public ResponseEntity<?> updateCartItem(@RequestBody Map<String, Integer> request) {
//        try {
//            cartService.updateCartItemQuantity(request.get("cart_id"), request.get("quantity"));
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/user/remove-cart-item/{cartId}")
//    public ResponseEntity<?> removeCartItem(@PathVariable Integer cartId) {
//        try {
//            cartService.removeCartItem(cartId);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/user/clear-cart/{userId}")
//    public ResponseEntity<?> clearCart(@PathVariable Integer userId) {
//        try {
//            cartService.clearCart(userId);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//}

