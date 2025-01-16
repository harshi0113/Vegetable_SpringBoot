package com.yash.Vegetabledeliveryonline;



import com.yash.Vegetabledeliveryonline.controller.AdminController;
import com.yash.Vegetabledeliveryonline.domain.User;
import com.yash.Vegetabledeliveryonline.service.UserService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Test User");
        testUser.setLoginName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(1); // Assuming 1 is for buyer role
    }

    @Test
    @DisplayName("Should get total users count successfully")
    void testGetTotalUsers() {
        // Given
        when(userService.getTotalUsers()).thenReturn(10L);

        // When
        ResponseEntity<Long> response = adminController.getTotalUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody());
        verify(userService).getTotalUsers();
    }

    @Test
    @DisplayName("Should handle exception when getting total users")
    void testGetTotalUsersException() {
        // Given
        when(userService.getTotalUsers()).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<Long> response = adminController.getTotalUsers();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should get buyer list successfully")
    void testGetBuyerList() {
        // Given
        List<User> buyers = Arrays.asList(testUser);
        when(userService.getBuyerList()).thenReturn(buyers);

        // When
        ResponseEntity<List<User>> response = adminController.getBuyerList();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(buyers, response.getBody());
        verify(userService).getBuyerList();
    }

    @Test
    @DisplayName("Should handle exception when getting buyer list")
    void testGetBuyerListException() {
        // Given
        when(userService.getBuyerList()).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<List<User>> response = adminController.getBuyerList();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should get seller list successfully")
    void testGetSellerList() {
        // Given
        List<User> sellers = Arrays.asList(testUser);
        when(userService.getSellerList()).thenReturn(sellers);

        // When
        ResponseEntity<List<User>> response = adminController.getSellerList();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellers, response.getBody());
        verify(userService).getSellerList();
    }

    @Test
    @DisplayName("Should handle exception when getting seller list")
    void testGetSellerListException() {
        // Given
        when(userService.getSellerList()).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<List<User>> response = adminController.getSellerList();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should change user status successfully")
    void testChangeUserStatus() {
        // Given
        Long userId = 1L;
        Integer newStatus = 1;
        doNothing().when(userService).changeLoginStatus(userId, newStatus);

        // When
        ResponseEntity<?> response = adminController.changeUserStatus(userId, newStatus);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User status updated successfully", responseBody.get("message"));
        verify(userService).changeLoginStatus(userId, newStatus);
    }

    @Test
    @DisplayName("Should handle exception when changing user status")
    void testChangeUserStatusException() {
        // Given
        Long userId = 1L;
        Integer newStatus = 1;
        doThrow(new RuntimeException("Update failed")).when(userService).changeLoginStatus(userId, newStatus);

        // When
        ResponseEntity<?> response = adminController.changeUserStatus(userId, newStatus);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.get("error").contains("Failed to update user status"));
    }

    @Test
    @DisplayName("Should get user image successfully")
    void testGetUserImage() {
        // Given
        byte[] imageData = "test image data".getBytes();
        testUser.setImage(imageData);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        ResponseEntity<byte[]> response = adminController.getUserImage(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(imageData, response.getBody());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should return not found when user image doesn't exist")
    void testGetUserImageNotFound() {
        // Given
        when(userService.findById(1L)).thenReturn(null);

        // When
        ResponseEntity<byte[]> response = adminController.getUserImage(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle exception when getting user image")
    void testGetUserImageException() {
        // Given
        when(userService.findById(1L)).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<byte[]> response = adminController.getUserImage(1L);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle empty image data correctly")
    void testGetUserImageEmptyData() {
        // Given
        testUser.setImage(new byte[0]);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        ResponseEntity<byte[]> response = adminController.getUserImage(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}