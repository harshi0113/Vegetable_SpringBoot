package com.yash.Vegetabledeliveryonline.controller;


import com.yash.Vegetabledeliveryonline.domain.Menu;
import com.yash.Vegetabledeliveryonline.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<Menu> createMenu(@RequestPart Menu menu,
                                           @RequestPart(required = false) MultipartFile image) throws IOException {
        if (menu.getShop() == null || menu.getShop().getId() == null) {
            throw new IllegalArgumentException("Shop must be provided with a valid ID");
        }
        return ResponseEntity.ok(menuService.saveMenu(menu, image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long menuId,
                                           @RequestPart Menu menu,
                                           @RequestPart(required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(menuService.updateMenu(menuId, menu, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenu(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.getMenuById(menuId));

    }

    @GetMapping("/shop/{id}")
    public ResponseEntity<List<Menu>> getMenusByShop(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenusByShopId(id));
    }

    @GetMapping(value = "/image/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getMenuImage(@PathVariable Long id) {
        try {
            Menu menu = menuService.getMenuById(id);

            if (menu == null || menu.getImage() == null || menu.getImage().length == 0) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(menu.getImage().length);
            headers.setCacheControl("public, max-age=86400"); // Cache for 24 hours

            return new ResponseEntity<>(menu.getImage(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

