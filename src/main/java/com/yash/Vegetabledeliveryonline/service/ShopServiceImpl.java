package com.yash.Vegetabledeliveryonline.service;

import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    @Override
    public Shop saveShop(Shop shop) {
        return shopRepository.save(shop);
    }

    @Override
    public Shop updateShop(Shop shop) {
        return shopRepository.save(shop);
    }

    @Override
    public void deleteShop(Long id) {
        shopRepository.deleteById(id);
    }

    @Override
    public Shop findById(Long id) {
        return shopRepository.findById(id).orElse(null);
    }

    @Override
    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    @Override
    public List<Shop> findByUserId(Long userId) {
        return shopRepository.findByUserId(userId);
    }

    @Override
    public boolean hasExistingShop(Long userId) {
        return shopRepository.existsByUserId(userId);
    }

//    @Override
//    public Long getTotalOrdersForUser(Long userId) {
//        // Implement order counting logic
//        return orderRepository.countByUserId(userId);
//    }

//    @Override
//    public Long getPendingOrdersForUser(Long userId) {
//        // Implement pending orders counting logic
//        return orderRepository.countByUserIdAndStatus(userId, "PENDING");
//    }

//    @Override
//    public Long getCompletedOrdersForUser(Long userId) {
//        // Implement completed orders counting logic
//        return orderRepository.countByUserIdAndStatus(userId, "COMPLETED");
//    }

//    @Override
//    public Double getTotalSpentByUser(Long userId) {
//        // Implement total spent calculation logic
//        return orderRepository.sumTotalAmountByUserId(userId);
//    }

    @Override
    public List<Shop> getAllShops() {
        return shopRepository.findAll(); // Simply return all shops
    }
}
