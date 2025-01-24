package com.yash.Vegetabledeliveryonline;



import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.repository.ShopRepository;
import com.yash.Vegetabledeliveryonline.service.ShopServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ShopServiceImpl shopService;

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
    @DisplayName("Should Save New Shop Successfully")
    void testSaveShop() {
        // Given
        when(shopRepository.save(any(Shop.class)))
                .thenReturn(testShop);

        // When
        Shop savedShop = shopService.saveShop(testShop);

        // Then
        assertNotNull(savedShop);
        assertEquals(testShop.getName(), savedShop.getName());
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    @DisplayName("Should Update Existing Shop")
    void testUpdateShop() {
        // Given
        testShop.setName("Updated Shop Name");
        when(shopRepository.save(any(Shop.class)))
                .thenReturn(testShop);

        // When
        Shop updatedShop = shopService.updateShop(testShop);

        // Then
        assertEquals("Updated Shop Name", updatedShop.getName());
        verify(shopRepository).save(testShop);
    }

    @Test
    @DisplayName("Should Find Shop By ID")
    void testFindById() {
        // Given
        when(shopRepository.findById(1L))
                .thenReturn(Optional.of(testShop));

        // When
        Shop foundShop = shopService.findById(1L);

        // Then
        assertNotNull(foundShop);
        assertEquals(testShop.getId(), foundShop.getId());
    }

    @Test
    @DisplayName("Should Find All Shops By User ID")
    void testFindByUserId() {
        // Given
        List<Shop> shops = Arrays.asList(testShop);
        when(shopRepository.findByUserId(1L))
                .thenReturn(shops);

        // When
        List<Shop> foundShops = shopService.findByUserId(1L);

        // Then
        assertFalse(foundShops.isEmpty());
        assertEquals(1, foundShops.size());
    }

    @Test
    @DisplayName("Should Check For Existing Shop")
    void testHasExistingShop() {
        // Given
        when(shopRepository.existsByUserId(1L))
                .thenReturn(true);

        // When
        boolean hasShop = shopService.hasExistingShop(1L);

        // Then
        assertTrue(hasShop);
    }

    @Test
    @DisplayName("Should Delete Shop")
    void testDeleteShop() {
        // When
        shopService.deleteShop(1L);

        // Then
        verify(shopRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should Find All Shops")
    void testFindAllShops() {
        // Given
        List<Shop> shops = Arrays.asList(testShop);
        when(shopRepository.findAll())
                .thenReturn(shops);

        // When
        List<Shop> allShops = shopService.getAllShops();

        // Then
        assertFalse(allShops.isEmpty());
        assertEquals(1, allShops.size());
    }

    @Test
    @DisplayName("Should Find All Shops")
    void testFindAll() {
        // Given
        Shop anotherShop = new Shop();
        anotherShop.setId(2L);
        anotherShop.setUserId(2L);
        anotherShop.setName("Another Shop");
        anotherShop.setAddress("456 Street");
        anotherShop.setPhone("9876543210");
        anotherShop.setEmail("another@test.com");

        List<Shop> shops = Arrays.asList(testShop, anotherShop);
        when(shopRepository.findAll())
                .thenReturn(shops);

        // When
        List<Shop> allShops = shopService.findAll();

        // Then
        assertNotNull(allShops);
        assertFalse(allShops.isEmpty());
        assertEquals(2, allShops.size());
        verify(shopRepository).findAll();
    }


}

