package com.yash.Vegetabledeliveryonline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.yash.Vegetabledeliveryonline.controller.DashboardController;
import com.yash.Vegetabledeliveryonline.service.ShopService;
import com.yash.Vegetabledeliveryonline.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;

@ExtendWith(SpringExtension.class)
class DashboardControllerDiffblueTest {

    @Mock
    private UserService userService;

    @Mock
    private ShopService shopService;

    @InjectMocks
    private DashboardController dashboardController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    @Test
    void getUserDashboardStats_Success() {
        // Arrange
        Long userId = 1L;

        // Act
        ResponseEntity<?> response = dashboardController.getUserDashboardStats(userId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
    }

//    @Test
//    void getUserDashboardStats_Error() {
//        // Arrange
//        Long userId = 1L;
//        when(userService.findById(userId)).thenThrow(new RuntimeException("User not found"));
//
//        // Act
//        ResponseEntity<?> response = dashboardController.getUserDashboardStats(userId);
//
//        // Assert
//        assertEquals(400, response.getStatusCodeValue());
//        assertEquals("Error fetching dashboard stats: User not found", response.getBody());
//    }

    @Test
    void getAllShops_Success() throws Exception {
        // Arrange
        when(shopService.getAllShops()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/shops"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void getAllShops_DirectResponseEntity_Success() {
        // Arrange
        when(shopService.getAllShops()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<?> response = dashboardController.getAllShops();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof ArrayList);
    }

    @Test
    void getAllShops_Error() {
        // Arrange
        when(shopService.getAllShops()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = dashboardController.getAllShops();

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error fetching shops: Database error", response.getBody());
    }

    @Test
    void getAllShops_ErrorWithMockMvc() throws Exception {
        // Arrange
        when(shopService.getAllShops()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/shops"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error fetching shops: Database error"));
    }

    @Test
    @DisplayName("dashboard stats error")
    void testDashboardStatsError() throws Exception{

        when(shopService.getAllShops()).thenThrow(new RuntimeException("dashboard data error"));

        mockMvc.perform(get("/api/dashboard/shops"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("helllo"));
    }
}