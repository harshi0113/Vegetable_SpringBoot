package com.yash.Vegetabledeliveryonline;


import com.yash.Vegetabledeliveryonline.domain.Menu;
import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MenuRepositoryTest {

    @Mock
    private MenuRepository menuRepository;

    private Menu testMenu;
    private Shop testShop;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);

        testMenu = new Menu();
        testMenu.setMenuId(1L);
        testMenu.setItemName("Carrots");
        testMenu.setDescription("Fresh Carrots");
        testMenu.setPrice(2.99);
        testMenu.setVegetableCategory("Root Vegetables");
        testMenu.setShop(testShop);
    }

    @Test
    @DisplayName("Should find menus by shop id")
    void testFindByShopId() {
        List<Menu> expectedMenus = Arrays.asList(testMenu);
        when(menuRepository.findByShopId(1L)).thenReturn(expectedMenus);

        List<Menu> actualMenus = menuRepository.findByShopId(1L);

        assertEquals(expectedMenus, actualMenus);
        verify(menuRepository).findByShopId(1L);
    }

    @Test
    @DisplayName("Should save menu")
    void testSave() {
        when(menuRepository.save(any(Menu.class))).thenReturn(testMenu);

        Menu savedMenu = menuRepository.save(testMenu);

        assertNotNull(savedMenu);
        assertEquals(testMenu, savedMenu);
        verify(menuRepository).save(testMenu);
    }

    @Test
    @DisplayName("Should find menu by id")
    void testFindById() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));

        Optional<Menu> foundMenu = menuRepository.findById(1L);

        assertTrue(foundMenu.isPresent());
        assertEquals(testMenu, foundMenu.get());
    }

    @Test
    @DisplayName("Should return empty when menu not found")
    void testFindByIdNotFound() {
        when(menuRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Menu> foundMenu = menuRepository.findById(1L);

        assertFalse(foundMenu.isPresent());
    }
}
