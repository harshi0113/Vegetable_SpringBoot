package com.yash.Vegetabledeliveryonline.repository;


import com.yash.Vegetabledeliveryonline.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
