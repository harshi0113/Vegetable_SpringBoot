package com.yash.Vegetabledeliveryonline;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yash.Vegetabledeliveryonline.domain.Menu;
import com.yash.Vegetabledeliveryonline.domain.Shop;
import com.yash.Vegetabledeliveryonline.repository.MenuRepository;
import com.yash.Vegetabledeliveryonline.repository.ShopRepository;
import com.yash.Vegetabledeliveryonline.service.MenuServiceImpl;
import jakarta.persistence.EntityNotFoundException;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {MenuServiceImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class MenuServiceImplDiffblueTest {
    @MockBean
    private MenuRepository menuRepository;

    @Autowired
    private MenuServiceImpl menuServiceImpl;

    @MockBean
    private ShopRepository shopRepository;

    @Test
    @DisplayName("Test deleteMenu(Long); given MenuRepository deleteById(Object) does nothing")
    void testDeleteMenu_givenMenuRepositoryDeleteByIdDoesNothing() {
        // Arrange
        doNothing().when(menuRepository).deleteById(Mockito.<Long>any());

        // Act
        menuServiceImpl.deleteMenu(1L);

        // Assert that nothing has changed
        verify(menuRepository).deleteById(eq(1L));
    }

    @Test
    @DisplayName("Test deleteMenu(Long); then throw EntityNotFoundException")
    void testDeleteMenu_thenThrowEntityNotFoundException() {
        // Arrange
        doThrow(new EntityNotFoundException("An error occurred")).when(menuRepository).deleteById(Mockito.<Long>any());

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> menuServiceImpl.deleteMenu(1L));
        verify(menuRepository).deleteById(eq(1L));
    }

    @Test
    @DisplayName("Test getMenuById(Long); given MenuRepository findById(Object) return empty; then throw RuntimeException")
    void testGetMenuById_givenMenuRepositoryFindByIdReturnEmpty_thenThrowRuntimeException() {
        // Arrange
        Optional<Menu> emptyResult = Optional.empty();
        when(menuRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> menuServiceImpl.getMenuById(1L));
        verify(menuRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Test getMenuById(Long); given Shop (default constructor) Address is '42 Main St'; then return Menu (default constructor)")
    void testGetMenuById_givenShopAddressIs42MainSt_thenReturnMenu() throws UnsupportedEncodingException {
        // Arrange
        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setEmail("jane.doe@example.org");
        shop.setId(1L);
        shop.setName("Name");
        shop.setPhone("6625550144");
        shop.setRemark("Remark");
        shop.setUserId(1L);

        Menu menu = new Menu();
        menu.setDescription("The characteristics of someone or something");
        menu.setImage("AXAXAXAX".getBytes("UTF-8"));
        menu.setItemName("Item Name");
        menu.setMenuId(1L);
        menu.setPrice(10.0d);
        menu.setShop(shop);
        menu.setVegetableCategory("Vegetable Category");
        Optional<Menu> ofResult = Optional.of(menu);
        when(menuRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        Menu actualMenuById = menuServiceImpl.getMenuById(1L);

        // Assert
        verify(menuRepository).findById(eq(1L));
        assertSame(menu, actualMenuById);
    }

    @Test
    @DisplayName("Test getMenuById(Long); then throw EntityNotFoundException")
    void testGetMenuById_thenThrowEntityNotFoundException() {
        // Arrange
        when(menuRepository.findById(Mockito.<Long>any())).thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> menuServiceImpl.getMenuById(1L));
        verify(menuRepository).findById(eq(1L));
    }


    @Test
    @DisplayName("Test getAllMenus(); given MenuRepository findAll() return ArrayList(); then return Empty")
    void testGetAllMenus_givenMenuRepositoryFindAllReturnArrayList_thenReturnEmpty() {
        // Arrange
        when(menuRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<Menu> actualAllMenus = menuServiceImpl.getAllMenus();

        // Assert
        verify(menuRepository).findAll();
        assertTrue(actualAllMenus.isEmpty());
    }


    @Test
    @DisplayName("Test getAllMenus(); then throw EntityNotFoundException")
    void testGetAllMenus_thenThrowEntityNotFoundException() {
        // Arrange
        when(menuRepository.findAll()).thenThrow(new EntityNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> menuServiceImpl.getAllMenus());
        verify(menuRepository).findAll();
    }


    @Test
    @DisplayName("Test getMenuImage(Long); given Menu getImage() return empty array of byte; then calls getImage()")
    void testGetMenuImage_givenMenuGetImageReturnEmptyArrayOfByte_thenCallsGetImage()
            throws UnsupportedEncodingException {
        // Arrange
        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setEmail("jane.doe@example.org");
        shop.setId(1L);
        shop.setName("Name");
        shop.setPhone("6625550144");
        shop.setRemark("Remark");
        shop.setUserId(1L);
        Menu menu = mock(Menu.class);
        when(menu.getImage()).thenReturn(new byte[]{});
        doNothing().when(menu).setDescription(Mockito.<String>any());
        doNothing().when(menu).setImage(Mockito.<byte[]>any());
        doNothing().when(menu).setItemName(Mockito.<String>any());
        doNothing().when(menu).setMenuId(Mockito.<Long>any());
        doNothing().when(menu).setPrice(Mockito.<Double>any());
        doNothing().when(menu).setShop(Mockito.<Shop>any());
        doNothing().when(menu).setVegetableCategory(Mockito.<String>any());
        menu.setDescription("The characteristics of someone or something");
        menu.setImage("AXAXAXAX".getBytes("UTF-8"));
        menu.setItemName("Item Name");
        menu.setMenuId(1L);
        menu.setPrice(10.0d);
        menu.setShop(shop);
        menu.setVegetableCategory("Vegetable Category");
        Optional<Menu> ofResult = Optional.of(menu);
        when(menuRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> menuServiceImpl.getMenuImage(1L));
        verify(menu, atLeast(1)).getImage();
        verify(menu).setDescription(eq("The characteristics of someone or something"));
        verify(menu).setImage(isA(byte[].class));
        verify(menu).setItemName(eq("Item Name"));
        verify(menu).setMenuId(eq(1L));
        verify(menu).setPrice(eq(10.0d));
        verify(menu).setShop(isA(Shop.class));
        verify(menu).setVegetableCategory(eq("Vegetable Category"));
        verify(menuRepository).findById(eq(1L));
    }


    @Test
    @DisplayName("Test getMenuImage(Long); given MenuRepository findById(Object) return empty")
    void testGetMenuImage_givenMenuRepositoryFindByIdReturnEmpty() {
        // Arrange
        Optional<Menu> emptyResult = Optional.empty();
        when(menuRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> menuServiceImpl.getMenuImage(1L));
        verify(menuRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("Test getMenuImage(Long); then return 'AXAXAXAX' Bytes is 'UTF-8'")
    void testGetMenuImage_thenReturnAxaxaxaxBytesIsUtf8() throws UnsupportedEncodingException {
        // Arrange
        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setEmail("jane.doe@example.org");
        shop.setId(1L);
        shop.setName("Name");
        shop.setPhone("6625550144");
        shop.setRemark("Remark");
        shop.setUserId(1L);

        Menu menu = new Menu();
        menu.setDescription("The characteristics of someone or something");
        menu.setImage("AXAXAXAX".getBytes("UTF-8"));
        menu.setItemName("Item Name");
        menu.setMenuId(1L);
        menu.setPrice(10.0d);
        menu.setShop(shop);
        menu.setVegetableCategory("Vegetable Category");
        Optional<Menu> ofResult = Optional.of(menu);
        when(menuRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        byte[] actualMenuImage = menuServiceImpl.getMenuImage(1L);

        // Assert
        verify(menuRepository).findById(eq(1L));
        assertArrayEquals("AXAXAXAX".getBytes("UTF-8"), actualMenuImage);
    }
}
