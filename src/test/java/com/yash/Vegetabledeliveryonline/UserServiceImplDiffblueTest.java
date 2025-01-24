package com.yash.Vegetabledeliveryonline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.yash.Vegetabledeliveryonline.domain.User;
import com.yash.Vegetabledeliveryonline.exception.UserBlockedException;
import com.yash.Vegetabledeliveryonline.repository.UserRepository;
import com.yash.Vegetabledeliveryonline.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplDiffblueTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setLoginName("testUser");
        testUser.setPassword("password");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(1);
        testUser.setLoginStatus(1);
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByLoginName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.register(testUser);

        assertNotNull(result);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_MissingRequiredFields() {
        testUser.setLoginName(null);
        assertThrows(IllegalArgumentException.class, () -> userService.register(testUser));

        testUser.setLoginName("testUser");
        testUser.setPassword(null);
        assertThrows(IllegalArgumentException.class, () -> userService.register(testUser));
    }

    @Test
    void testRegister_DuplicateUsername() {
        when(userRepository.existsByLoginName(anyString())).thenReturn(true);
        assertThrows(DuplicateKeyException.class, () -> userService.register(testUser));
    }

    @Test
    void testLogin_Success() throws UserBlockedException {
        when(userRepository.findByLoginName(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        User result = userService.login("testUser", "password");

        assertNotNull(result);
        assertEquals(testUser.getLoginName(), result.getLoginName());
    }

    @Test
    void testLogin_UserBlocked() {
        testUser.setLoginStatus(2); // Assuming 2 is blocked status
        when(userRepository.findByLoginName(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(UserBlockedException.class, () -> userService.login("testUser", "password"));
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(userRepository.findByLoginName(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> userService.login("testUser", "wrongPassword"));
    }


    @Test
    void testGetSellerList() {
        List<User> sellers = Arrays.asList(testUser);
        when(userRepository.findByRole(eq(2))).thenReturn(sellers);

        List<User> result = userService.getSellerList();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testChangeLoginStatus_UserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changeLoginStatus(1L, 2);

        verify(userRepository).save(any(User.class));
        assertEquals(2, testUser.getLoginStatus());
    }

    @Test
    void testChangeLoginStatus_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        userService.changeLoginStatus(1L, 2);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testIsUsernameExist() {
        when(userRepository.existsByLoginName(anyString())).thenReturn(true);

        boolean result = userService.isUsernameExist("testUser");

        assertTrue(result);
        verify(userRepository).existsByLoginName("testUser");
    }

    @Test
    void testGetTotalUsers() {
        when(userRepository.count()).thenReturn(5L);

        long result = userService.getTotalUsers();

        assertEquals(5L, result);
        verify(userRepository).count();
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findById(1L));
    }

    @Test
    void testFindByLoginName_Success() {
        when(userRepository.findByLoginName(anyString())).thenReturn(Optional.of(testUser));

        User result = userService.findByLoginName("testUser");

        assertNotNull(result);
        assertEquals(testUser.getLoginName(), result.getLoginName());
    }

    @Test
    void testFindByLoginName_UserNotFound() {
        when(userRepository.findByLoginName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findByLoginName("nonexistent"));
    }
}