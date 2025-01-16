//package com.yash.Vegetabledeliveryonline.service;
//
//import com.yash.Vegetabledeliveryonline.domain.Cart;
//import com.yash.Vegetabledeliveryonline.domain.Menu;
//import com.yash.Vegetabledeliveryonline.domain.User;
//import com.yash.Vegetabledeliveryonline.domain.Shop;
//import com.yash.Vegetabledeliveryonline.repository.CartRepository;
//import com.yash.Vegetabledeliveryonline.repository.MenuRepository;
//import com.yash.Vegetabledeliveryonline.repository.UserRepository;
//import com.yash.Vegetabledeliveryonline.repository.ShopRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class CartServiceImpl implements CartService {
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private MenuRepository menuRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ShopRepository shopRepository;
//
//    public void addToCart(Integer userId, Integer menuId, Integer quantity, Integer contactId) {
//        Menu menu = menuRepository.findById(menuId.longValue())
//                .orElseThrow(() -> new RuntimeException("Menu item not found"));
//
//        User user = userRepository.findById(userId.longValue())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Shop shop = shopRepository.findById(contactId.longValue())
//                .orElseThrow(() -> new RuntimeException("Shop not found"));
//
//        Cart existingCart = cartRepository.findByUserIdAndMenuId(userId, menuId);
//
//        if (existingCart != null) {
//            existingCart.setQuantity(quantity);
//            existingCart.setTotalPrice(menu.getPrice() * quantity);
//            cartRepository.save(existingCart);
//        } else {
//            Cart cart = new Cart();
//            cart.setUser(user);
//            cart.setMenu(menu);
//            cart.setShop(shop);
//            cart.setQuantity(quantity);
//            cart.setTotalPrice(menu.getPrice() * quantity);
//            cartRepository.save(cart);
//        }
//    }
//
//    public List<Cart> getCartItems(Integer userId) {
//        return cartRepository.findByUserId(userId);
//        // No need to explicitly set menu as it's already mapped via @ManyToOne
//    }
//
//    public void updateCartItemQuantity(Integer cartId, Integer quantity) {
//        Cart cart = cartRepository.findById(cartId)
//                .orElseThrow(() -> new RuntimeException("Cart item not found"));
//
//        cart.setQuantity(quantity);
//        cart.setTotalPrice(cart.getMenu().getPrice() * quantity);
//        cartRepository.save(cart);
//    }
//
//    public void removeCartItem(Integer cartId) {
//        cartRepository.deleteById(cartId);
//    }
//
//    public void clearCart(Integer userId) {
//        cartRepository.deleteByUserId(userId);
//    }
//}