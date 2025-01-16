package com.yash.Vegetabledeliveryonline.service;


import com.yash.Vegetabledeliveryonline.domain.Menu;
import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.repository.MenuRepository;
import com.yash.Vegetabledeliveryonline.repository.ShopRepository;
import com.yash.Vegetabledeliveryonline.service.MenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository; // Add this



    @Override
    public Menu saveMenu(Menu menu, MultipartFile image) throws IOException {

        Shop shop = shopRepository.findById(menu.getShop().getId())
                .orElseThrow(() -> new EntityNotFoundException("Shop not found with id: " + menu.getShop().getId()));

        menu.setShop(shop);

        if (image != null && !image.isEmpty()) {
            menu.setImage(image.getBytes());
        }
        return menuRepository.save(menu);
    }

    @Override
    public Menu updateMenu(Long menuId, Menu menu, MultipartFile image) throws IOException {
        Menu existingMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));

        existingMenu.setItemName(menu.getItemName());
        existingMenu.setDescription(menu.getDescription());
        existingMenu.setPrice(menu.getPrice());
        existingMenu.setVegetableCategory(menu.getVegetableCategory());

        if (image != null && !image.isEmpty()) {
            existingMenu.setImage(image.getBytes());
        }

        return menuRepository.save(existingMenu);
    }

    @Override
    public void deleteMenu(Long menuId) {
        menuRepository.deleteById(menuId);
    }

    @Override
    public Menu getMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    @Override
    public List<Menu> getMenusByShopId(Long id) {
        return menuRepository.findByShopId(id);
    }

    @Override
    public byte[] getMenuImage(Long menuId) {
        try {
            Menu menu = menuRepository.findById(menuId)
                    .orElseThrow(() -> new EntityNotFoundException("Menu not found with id: " + menuId));

            if (menu.getImage() == null || menu.getImage().length == 0) {
                throw new EntityNotFoundException("No image found for menu with id: " + menuId);
            }

            return menu.getImage();
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving menu image: " + e.getMessage());
        }
    }
}
