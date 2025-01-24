package com.yash.Vegetabledeliveryonline;


import com.yash.Vegetabledeliveryonline.controller.MenuController;
import com.yash.Vegetabledeliveryonline.domain.Menu;
import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

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
        testMenu.setVegetableCategory("Vegetable");
        testMenu.setShop(testShop);

        testImage = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
    }

    @Test
    @DisplayName("Should create menu successfully")
    void testCreateMenu() throws IOException {
        //Given
        when(menuService.saveMenu(any(Menu.class), any(MultipartFile.class)))
                .thenReturn(testMenu);

        //When
        ResponseEntity<Menu> response = menuController.createMenu(testMenu, testImage);

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMenu, response.getBody());
        verify(menuService).saveMenu(any(Menu.class), any(MultipartFile.class));
    }

    @Test
    @DisplayName("Should throw exception when creating menu without shop")
    void testCreateMenuWithoutShop() {
        testMenu.setShop(null);

        assertThrows(IllegalArgumentException.class,
                () -> menuController.createMenu(testMenu, testImage));
    }

    @Test
    @DisplayName("Should get menu by id")
    void testGetMenu() {
        //Given
        when(menuService.getMenuById(1L)).thenReturn(testMenu);

        //When
        ResponseEntity<Menu> response = menuController.getMenu(1L);

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMenu, response.getBody());
    }

    @Test
    @DisplayName("Should get menus by shop id")
    void testGetMenusByShop() {
        //Given
        List<Menu> menus = Arrays.asList(testMenu);
        when(menuService.getMenusByShopId(1L)).thenReturn(menus);

        //When
        ResponseEntity<List<Menu>> response = menuController.getMenusByShop(1L);

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(menus, response.getBody());
    }

    @Test
    @DisplayName("Should get menu image")
    void testGetMenuImage() {

        //Given
        testMenu.setImage("test image content".getBytes());
        when(menuService.getMenuById(1L)).thenReturn(testMenu);

        //When
        ResponseEntity<byte[]> response = menuController.getMenuImage(1L);

        //Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(testMenu.getImage(), response.getBody());
    }

    @Test
    @DisplayName("Should return not found for non-existent menu image")
    void testGetMenuImageNotFound() {

        //Given
        when(menuService.getMenuById(1L)).thenReturn(null);

        //When
        ResponseEntity<byte[]> response = menuController.getMenuImage(1L);

        //Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should update menu successfully")
    void testUpdateMenu() throws IOException {
        // Given
        Long menuId = 1L;
        Menu updatedMenu = new Menu();
        updatedMenu.setMenuId(menuId);
        updatedMenu.setItemName("Updated Carrots");
        updatedMenu.setDescription("Updated Fresh Carrots");
        updatedMenu.setPrice(3.99);
        updatedMenu.setShop(testShop);

        when(menuService.updateMenu(eq(menuId), any(Menu.class), any(MultipartFile.class)))
                .thenReturn(updatedMenu);

        // When
        ResponseEntity<Menu> response = menuController.updateMenu(menuId, updatedMenu, testImage);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedMenu, response.getBody());
        verify(menuService).updateMenu(eq(menuId), any(Menu.class), any(MultipartFile.class));
    }

    @Test
    @DisplayName("Should delete menu successfully")
    void testDeleteMenu() {
        // Given
        Long menuId = 1L;
        doNothing().when(menuService).deleteMenu(menuId);

        // When
        ResponseEntity<Void> response = menuController.deleteMenu(menuId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(menuService).deleteMenu(menuId);
    }

    @Test
    @DisplayName("Should handle internal server error when getting menu image")
    void testGetMenuImageInternalServerError() {
        // Given
        Long menuId = 1L;
        when(menuService.getMenuById(menuId)).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<byte[]> response = menuController.getMenuImage(menuId);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle null image when getting menu image")
    void testGetMenuImageNullImage() {
        // Given
        Long menuId = 1L;
        Menu menuWithoutImage = new Menu();
        menuWithoutImage.setMenuId(menuId);
        when(menuService.getMenuById(menuId)).thenReturn(menuWithoutImage);

        // When
        ResponseEntity<byte[]> response = menuController.getMenuImage(menuId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle empty image when getting menu image")
    void testGetMenuImageEmptyImage() {
        // Given
        Long menuId = 1L;
        Menu menuWithEmptyImage = new Menu();
        menuWithEmptyImage.setMenuId(menuId);
        menuWithEmptyImage.setImage(new byte[0]);
        when(menuService.getMenuById(menuId)).thenReturn(menuWithEmptyImage);

        // When
        ResponseEntity<byte[]> response = menuController.getMenuImage(menuId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should set correct headers when getting menu image")
    void testGetMenuImageHeaders() {
        // Given
        Long menuId = 1L;
        byte[] imageData = "test image content".getBytes();
        testMenu.setImage(imageData);
        when(menuService.getMenuById(menuId)).thenReturn(testMenu);

        // When
        ResponseEntity<byte[]> response = menuController.getMenuImage(menuId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals(imageData.length, response.getHeaders().getContentLength());
        assertEquals("public, max-age=86400", response.getHeaders().getCacheControl());
        assertArrayEquals(imageData, response.getBody());
    }
}

