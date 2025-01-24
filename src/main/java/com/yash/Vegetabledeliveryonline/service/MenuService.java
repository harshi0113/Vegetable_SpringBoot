package com.yash.Vegetabledeliveryonline.service;

import com.yash.Vegetabledeliveryonline.domain.Menu;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MenuService {
    Menu saveMenu(Menu menu, MultipartFile image) throws IOException;
    Menu updateMenu(Long menuId, Menu menu, MultipartFile image) throws IOException;
    void deleteMenu(Long menuId);
    Menu getMenuById(Long menuId);
    List<Menu> getAllMenus();
    List<Menu> getMenusByShopId(Long id);
    byte[] getMenuImage(Long menuId);
}