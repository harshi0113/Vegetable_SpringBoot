package com.yash.Vegetabledeliveryonline;

import com.yash.Vegetabledeliveryonline.controller.ShopController;
import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ShopRepositoryTest {

    @Mock
    private ShopService shopService;

    @InjectMocks
    private ShopController shopController;

    private Shop testShop;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setUserId(1L);
        testShop.setName("Test Vegetable Shop");
        testShop.setAddress("123 Market Street");
        testShop.setPhone("1234567890");
        testShop.setEmail("shop@test.com");
    }

    @Test
    @DisplayName("Should Get Dashboard Data Successfully")
    void testGetDashboardData() {
        // Given
        List<Shop> shops = Arrays.asList(testShop);
        when(shopService.findByUserId(1L)).thenReturn(shops);
        when(shopService.hasExistingShop(1L)).thenReturn(true);

        // When
        ResponseEntity<Map<String, Object>> response = shopController.getDashboardData(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        List<Shop> responseShops = (List<Shop>) response.getBody().get("shops");
        assertEquals(1, responseShops.size());
        assertTrue((Boolean) response.getBody().get("hasExistingShop"));
    }

    @Test
    @DisplayName("Should Create New Shop")
    void testCreateShop() {
        // Given
        when(shopService.hasExistingShop(1L)).thenReturn(false);
        when(shopService.saveShop(any(Shop.class))).thenReturn(testShop);

        // When
        ResponseEntity<?> response = shopController.createShop(testShop);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(shopService).saveShop(testShop);
    }

    @Test
    @DisplayName("Should Prevent Creating Duplicate Shop")
    void testCreateDuplicateShop() {
        // Given
        when(shopService.hasExistingShop(1L)).thenReturn(true);

        // When
        ResponseEntity<?> response = shopController.createShop(testShop);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already has a shop", response.getBody());
    }

    @Test
    @DisplayName("Should Update Existing Shop")
    void testUpdateShop() {
        // Given
        when(shopService.updateShop(any(Shop.class))).thenReturn(testShop);

        // When
        ResponseEntity<Shop> response = shopController.updateShop(1L, testShop);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testShop.getName(), response.getBody().getName());
    }

    @Test
    @DisplayName("Should Get Shop By ID")
    void testGetShop() {
        // Given
        when(shopService.findById(1L)).thenReturn(testShop);

        // When
        ResponseEntity<Shop> response = shopController.getShop(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testShop.getId(), response.getBody().getId());
    }

    @Test
    @DisplayName("Should Get User's Shops")
    void testGetUserShops() {
        // Given
        List<Shop> shops = Arrays.asList(testShop);
        when(shopService.findByUserId(1L)).thenReturn(shops);

        // When
        ResponseEntity<?> response = shopController.getUserShops(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should Delete Shop")
    void testDeleteShop() {
        // When
        ResponseEntity<?> response = shopController.deleteShop(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(shopService).deleteShop(1L);
    }
}