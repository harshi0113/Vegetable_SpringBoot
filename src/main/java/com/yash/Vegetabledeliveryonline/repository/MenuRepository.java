package com.yash.Vegetabledeliveryonline.repository;




import com.yash.Vegetabledeliveryonline.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByShopId(Long id);
}