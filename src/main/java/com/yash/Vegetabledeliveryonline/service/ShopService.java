package com.yash.Vegetabledeliveryonline.service;


import com.yash.Vegetabledeliveryonline.domain.Shop;

import java.util.List;

public interface ShopService {
    Shop saveShop(Shop shop);
    Shop updateShop(Shop shop);
    void deleteShop(Long id);
    Shop findById(Long id);
    List<Shop> findAll();
    List<Shop> findByUserId(Long userId);
    boolean hasExistingShop(Long userId);
    List<Shop> getAllShops();
}