package com.yash.Vegetabledeliveryonline.service;

import com.yash.Vegetabledeliveryonline.domain.Menu;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MenuService {
    Menu saveMenu(Menu menu, MultipartFile image) throws IOException;
    Menu updateMenu(Long menuId, Menu menu, MultipartFile image) throws IOException;  // Changed from id to menuId
    void deleteMenu(Long menuId);  // Changed from id to menuId
    Menu getMenuById(Long menuId);  // Changed from id to menuId
    List<Menu> getAllMenus();
    List<Menu> getMenusByShopId(Long id);  // This is correct as it refers to Shop's id
    byte[] getMenuImage(Long menuId);  // Changed from id to menuId
}