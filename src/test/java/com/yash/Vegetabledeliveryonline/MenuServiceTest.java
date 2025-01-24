package com.yash.Vegetabledeliveryonline;

import com.yash.Vegetabledeliveryonline.domain.Menu;
import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.repository.MenuRepository;
import com.yash.Vegetabledeliveryonline.repository.ShopRepository;
import com.yash.Vegetabledeliveryonline.service.MenuServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
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
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Captor
    private ArgumentCaptor<Menu> menuCaptor;

    private Menu testMenu;
    private Shop testShop;
    private MockMultipartFile testImage;

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

        testImage = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
    }

    @Test
    @DisplayName("Should save menu successfully")
    void testSaveMenu() throws IOException {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(menuRepository.save(any(Menu.class))).thenReturn(testMenu);

        Menu savedMenu = menuService.saveMenu(testMenu, testImage);

        verify(menuRepository).save(menuCaptor.capture());
        Menu capturedMenu = menuCaptor.getValue();

        assertNotNull(savedMenu);
        assertEquals(testMenu.getItemName(), capturedMenu.getItemName());
        assertArrayEquals(testImage.getBytes(), capturedMenu.getImage());
    }

    @Test
    @DisplayName("Should throw exception when shop not found")
    void testSaveMenuWithInvalidShop() {
        when(shopRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> menuService.saveMenu(testMenu, testImage));
    }

    @Test
    @DisplayName("Should update menu successfully")
    void testUpdateMenu() throws IOException {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(testMenu);

        Menu updatedMenu = menuService.updateMenu(1L, testMenu, testImage);

        verify(menuRepository).save(menuCaptor.capture());
        Menu capturedMenu = menuCaptor.getValue();

        assertNotNull(updatedMenu);
        assertEquals(testMenu.getItemName(), capturedMenu.getItemName());
        assertArrayEquals(testImage.getBytes(), capturedMenu.getImage());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent menu")
    void testUpdateNonExistentMenu() {
        when(menuRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> menuService.updateMenu(1L, testMenu, testImage));
    }

    @Test
    @DisplayName("Should get menus by shop id")
    void testGetMenusByShopId() {
        List<Menu> expectedMenus = Arrays.asList(testMenu);
        when(menuRepository.findByShopId(1L)).thenReturn(expectedMenus);

        List<Menu> actualMenus = menuService.getMenusByShopId(1L);

        assertEquals(expectedMenus, actualMenus);
        verify(menuRepository).findByShopId(1L);
    }

    @Test
    @DisplayName("Should get menu image")
    void testGetMenuImage() {
        testMenu.setImage("test image content".getBytes());
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));

        byte[] image = menuService.getMenuImage(1L);

        assertNotNull(image);
        assertArrayEquals(testMenu.getImage(), image);
    }


    @Test
    @DisplayName("Should save menu with null image")
    void testSaveMenuWithNullImage() throws IOException {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));
        when(menuRepository.save(any(Menu.class))).thenReturn(testMenu);

        Menu savedMenu = menuService.saveMenu(testMenu, null);

        assertNotNull(savedMenu);
        verify(menuRepository).save(testMenu);
    }

    @Test
    @DisplayName("Should update menu with null image")
    void testUpdateMenuWithNullImage() throws IOException {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(testMenu);

        Menu updatedMenu = menuService.updateMenu(1L, testMenu, null);

        assertNotNull(updatedMenu);
        verify(menuRepository).save(testMenu);
    }

    @Test
    @DisplayName("Should get all menus")
    void testGetAllMenus() {
        List<Menu> menuList = Arrays.asList(testMenu);
        when(menuRepository.findAll()).thenReturn(menuList);

        List<Menu> retrievedMenus = menuService.getAllMenus();

        assertFalse(retrievedMenus.isEmpty());
        assertEquals(1, retrievedMenus.size());
    }

    @Test
    @DisplayName("Should get menu by ID")
    void testGetMenuById() {
        when(menuRepository.findById(1L)).thenReturn(Optional.of(testMenu));

        Menu retrievedMenu = menuService.getMenuById(1L);

        assertNotNull(retrievedMenu);
        assertEquals(testMenu, retrievedMenu);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent menu")
    void testGetNonExistentMenuById() {
        when(menuRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> menuService.getMenuById(1L));
    }

    @Test
    @DisplayName("Should delete menu")
    void testDeleteMenu() {
        menuService.deleteMenu(1L);
        verify(menuRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when menu image is null")
    void testGetMenuImageNull() {
        Menu menuWithNullImage = new Menu();
        menuWithNullImage.setMenuId(1L);
        menuWithNullImage.setImage(null);

        when(menuRepository.findById(1L)).thenReturn(Optional.of(menuWithNullImage));

        assertThrows(EntityNotFoundException.class,
                () -> menuService.getMenuImage(1L));
    }

    @Test
    @DisplayName("Should handle unexpected exception in getMenuImage")
    void testGetMenuImageUnexpectedException() {
        when(menuRepository.findById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class,
                () -> menuService.getMenuImage(1L));
    }
}


