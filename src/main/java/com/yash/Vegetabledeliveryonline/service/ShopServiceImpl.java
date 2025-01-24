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

    @Override
    public List<Shop> getAllShops() {
        return shopRepository.findAll(); // Simply return all shops
    }
}
