package com.yash.Vegetabledeliveryonline;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import com.yash.Vegetabledeliveryonline.domain.User;
import com.yash.Vegetabledeliveryonline.repository.UserRepository;
import com.yash.Vegetabledeliveryonline.service.UserService;
import com.yash.Vegetabledeliveryonline.service.UserServiceImpl;
import com.yash.Vegetabledeliveryonline.controller.UserController;
import com.yash.Vegetabledeliveryonline.config.JwtService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

//Enables Mockito support for JUnit 5 tests
@ExtendWith(MockitoExtension.class)
class VegetabledeliveryonlineApplicationTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@Mock
	private AuthenticationManager authenticationManager;


	@Spy  //Creates a real object but allows mocking specific methods
	private List<User> userList = new ArrayList<>();

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	@Captor  // Captures arguments passed to methods
	private ArgumentCaptor<User> userCaptor;

	private User testUser;


	// class level setup runs once
	@BeforeAll // method should be  static as going to run once and bound to the particular test
	static void beforeAll() {
		// Run once before all tests
		System.out.println("Starting User Module Tests");
	}

	// setup for each test will run with each test
	@BeforeEach
	void setUp() {
		// Initialize test data before each test
		testUser = new User();
		testUser.setUserId(1L);  //1L means we're using a Long number with value 1
		testUser.setName("Test User");
		testUser.setLoginName("testuser");
		testUser.setPassword("password123");
		testUser.setEmail("test@example.com");
		testUser.setRole(userServiceImpl.ROLE_BUYER);
		testUser.setLoginStatus(userServiceImpl.LOGIN_STATUS_ACTIVE);
	}


	@AfterEach
	void tearDown() {
		// Cleanup after each test
		userList.clear();
	}

	@Nested
	@DisplayName("User Repository Tests")
	class UserRepositoryTests {

		//Test findByLoginName to ensure user lookup works
		@Test
		@DisplayName("Should find user by login name")
		void testFindByLoginName() {
			// Given
			given(userRepository.findByLoginName("testuser"))
					.willReturn(Optional.of(testUser));

			// When
			Optional<User> found = userRepository.findByLoginName("testuser");

			// Then
			assertTrue(found.isPresent());
			assertEquals("testuser", found.get().getLoginName());
			verify(userRepository, times(1)).findByLoginName(anyString());
		}

		//Test multiple save scenarios including error cases
		@Test
		@DisplayName("Should handle multiple saves")
		void testMultipleSaves() {
			// Demonstrates multiple interactions
			when(userRepository.save(any(User.class)))
					.thenReturn(testUser)
					.thenReturn(null)
					.thenThrow(new RuntimeException());

			// First save
			assertDoesNotThrow(() -> userRepository.save(testUser));

			// Second save
			assertNull(userRepository.save(testUser));

			// Third save
			assertThrows(RuntimeException.class, () -> userRepository.save(testUser));
		}

		//Test role-based user filtering
		@Test
		void testFindByRole() {
			// Repository layer test - tests finding users by role
			testUser.setRole(userServiceImpl.ROLE_BUYER);
			userRepository.save(testUser);

			List<User> sellers = userRepository.findByRole(userServiceImpl.ROLE_SELLER);
			assertTrue(sellers.isEmpty());
		}
	}

	@Nested
	@DisplayName("User Service Tests")
	class UserServiceTests {

		//Test user registration with password encoding
		@Test
		@DisplayName("Should register new user successfully")
		void testRegisterUser() {
			// Demonstrates argument captor usage
			when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
			when(userRepository.save(any(User.class))).thenReturn(testUser);

			userServiceImpl.register(testUser);

			verify(userRepository).save(userCaptor.capture());
			User capturedUser = userCaptor.getValue();
			assertEquals(testUser.getLoginName(), capturedUser.getLoginName());
		}

		@Test
		@DisplayName("Should handle registration failure")
		void testRegisterUserFailure() {
			// Demonstrates exception handling
			when(userRepository.save(any(User.class)))
					.thenThrow(new RuntimeException("Database error"));

			assertThrows(RuntimeException.class, () -> userServiceImpl.register(testUser));
		}

		//Test interaction counts with repository
		@Test
		@DisplayName("Should verify exact number of interactions")
		void testServiceInteractions() {
			// Demonstrates verification modes
			when(userRepository.findByRole(userServiceImpl.ROLE_BUYER))
					.thenReturn(Collections.singletonList(testUser));

			userServiceImpl.getBuyerList();
			userServiceImpl.getBuyerList();

			// Verify number of invocations
			verify(userRepository, times(2)).findByRole(anyInt());
			verify(userRepository, atLeast(1)).findByRole(anyInt());
			verify(userRepository, atMost(3)).findByRole(anyInt());
		}

		//Test user registration with password encoding
		@Test
		void testPasswordEncoding() {
			// Service layer test - tests password encoding
			when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
			when(userRepository.save(any(User.class))).thenReturn(testUser);

			testUser.setPassword("password123");
			userServiceImpl.register(testUser);

			verify(passwordEncoder).encode("password123");
			verify(userRepository).save(any(User.class));
		}

		//Test validation of user data
		@Test
		void testUserValidation() {
			// Service layer test - tests user validation
			User invalidUser = new User();
			// Test without required fields
			assertThrows(IllegalArgumentException.class, () -> userServiceImpl.register(invalidUser));
		}

		//Test duplicate username handling
		@Test
		void testDuplicateUsername() {
			// Service layer test - tests duplicate username handling
			when(userRepository.existsByLoginName(anyString())).thenReturn(true);

			testUser.setLoginName("existing_user");
			assertThrows(DuplicateKeyException.class, () -> userServiceImpl.register(testUser));
		}
	}

	@Nested
	@DisplayName("Registration Tests")
	class RegistrationTests {

		@Test
		@DisplayName("Should successfully register user with image")
		void testRegisterWithImage() throws Exception {
			MockMultipartFile imageFile = new MockMultipartFile(
					"imageFile",
					"test.jpg",
					MediaType.IMAGE_JPEG_VALUE,
					"test image content".getBytes()
			);

			when(userService.register(any(User.class))).thenAnswer(i -> {
				User user = i.getArgument(0);
				user.setUserId(1L);
				return user;
			});

			ResponseEntity<?> response = userController.register(testUser, imageFile);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			verify(userService).register(argThat(user ->
					user.getImage() != null &&
							user.getLoginStatus().equals(userService.LOGIN_STATUS_ACTIVE)
			));
		}

		@Test
		@DisplayName("Should register user without image")
		void testRegisterWithoutImage() throws Exception {
			when(userService.register(any(User.class))).thenReturn(testUser);

			ResponseEntity<?> response = userController.register(testUser, null);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			verify(userService).register(argThat(user ->
					user.getImage() == null &&
							user.getLoginStatus().equals(userService.LOGIN_STATUS_ACTIVE)
			));
		}

		@Test
		@DisplayName("Should handle duplicate username")
		void testRegisterDuplicateUsername() throws Exception {
			when(userService.register(any(User.class)))
					.thenThrow(new DuplicateKeyException("Duplicate username"));

			ResponseEntity<?> response = userController.register(testUser, null);

			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			@SuppressWarnings("unchecked")
			Map<String, String> responseBody = (Map<String, String>) response.getBody();
			assertEquals("Username already exists", responseBody.get("error"));
		}

		@Test
		@DisplayName("Should successfully register seller with image")
		void testRegisterSellerWithImage() throws Exception {
			// Setup seller user
			testUser.setRole(userService.ROLE_SELLER);

			MockMultipartFile imageFile = new MockMultipartFile(
					"imageFile",
					"test.jpg",
					MediaType.IMAGE_JPEG_VALUE,
					"test image content".getBytes()
			);

			when(userService.register(any(User.class))).thenAnswer(i -> {
				User user = i.getArgument(0);
				user.setUserId(1L);
				return user;
			});

			ResponseEntity<?> response = userController.register(testUser, imageFile);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			verify(userService).register(argThat(user ->
					user.getImage() != null &&
							user.getRole().equals(userService.ROLE_SELLER) &&
							user.getLoginStatus().equals(userService.LOGIN_STATUS_BLOCKED)
			));
		}


		@Test
		@DisplayName("Should register buyer without image")
		void testRegisterBuyerWithoutImage() throws Exception {
			// Setup buyer user
			testUser.setRole(userService.ROLE_BUYER);

			when(userService.register(any(User.class))).thenReturn(testUser);

			ResponseEntity<?> response = userController.register(testUser, null);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			verify(userService).register(argThat(user ->
					user.getImage() == null &&
							user.getRole().equals(userService.ROLE_BUYER) &&
							user.getLoginStatus().equals(userService.LOGIN_STATUS_ACTIVE)
			));
		}


//		@Test
//		@DisplayName("Should handle image processing error for seller")
//		void testRegisterImageProcessingError() throws Exception {
//			// Setup seller user
//			testUser.setRole(userService.ROLE_SELLER);
//
//			MockMultipartFile imageFile = new MockMultipartFile(
//					"imageFile",
//					"test.jpg",
//					MediaType.IMAGE_JPEG_VALUE,
//					new byte[0]
//			);
//
//			when(userService.register(any(User.class)))
//					.thenThrow(new IOException("Error processing image"));
//
//			ResponseEntity<?> response = userController.register(testUser, imageFile);
//
//			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//			@SuppressWarnings("unchecked")
//			Map<String, String> responseBody = (Map<String, String>) response.getBody();
//			assertEquals("Error processing image file", responseBody.get("error"));
//		}

		@Test
		@DisplayName("Should handle general registration error")
		void testRegisterGeneralError() throws Exception {
			when(userService.register(any(User.class)))
					.thenThrow(new RuntimeException("Database connection failed"));

			ResponseEntity<?> response = userController.register(testUser, null);

			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			@SuppressWarnings("unchecked")
			Map<String, String> responseBody = (Map<String, String>) response.getBody();
			assertEquals("Registration failed: Database connection failed", responseBody.get("error"));
		}


		@Test
		@DisplayName("Should register seller with blocked status")
		void testRegisterSeller() throws Exception {
			testUser.setRole(userService.ROLE_SELLER);
			when(userService.register(any(User.class))).thenReturn(testUser);

			ResponseEntity<?> response = userController.register(testUser, null);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			verify(userService).register(argThat(user ->
					user.getLoginStatus().equals(userService.LOGIN_STATUS_BLOCKED)
			));
		}


		@Nested
		@DisplayName("Login Tests")
		class LoginTests {

			@Test
			@DisplayName("Should successfully login active user")
			void testSuccessfulLogin() {
				User activeUser = new User();
				activeUser.setUserId(1L);
				activeUser.setName("Active User");
				activeUser.setLoginName("activeuser");
				activeUser.setPassword("activepass123");
				activeUser.setEmail("active@example.com");
				activeUser.setRole(userService.ROLE_BUYER);
				activeUser.setLoginStatus(userService.LOGIN_STATUS_ACTIVE);

				Map<String, String> credentials = new HashMap<>();
				credentials.put("loginName", "activeuser");
				credentials.put("password", "activepass123");

				when(userService.findByLoginName("activeuser")).thenReturn(activeUser);
				when(jwtService.generateToken(activeUser)).thenReturn("test.jwt.token");
				when(authenticationManager.authenticate(any())).thenReturn(
						new UsernamePasswordAuthenticationToken(activeUser, null)
				);

				ResponseEntity<?> response = userController.login(credentials);

				assertEquals(HttpStatus.OK, response.getStatusCode());
				@SuppressWarnings("unchecked")
				Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
				assertNotNull(responseBody);
				assertNotNull(responseBody.get("token"));
				assertEquals("test.jwt.token", responseBody.get("token"));
				@SuppressWarnings("unchecked")
				Map<String, Object> userInfo = (Map<String, Object>) responseBody.get("user");
				assertEquals(activeUser.getName(), userInfo.get("name"));
				assertEquals(activeUser.getEmail(), userInfo.get("email"));
				assertEquals(activeUser.getRole(), userInfo.get("role"));
				assertEquals(activeUser.getUserId(), userInfo.get("userId"));
			}

			@Test
			@DisplayName("Should reject login for blocked user")
			void testBlockedUserLogin() {
				User blockedUser = new User();
				blockedUser.setUserId(2L);
				blockedUser.setName("Blocked User");
				blockedUser.setLoginName("blockeduser");
				blockedUser.setPassword("blockedpass123");
				blockedUser.setEmail("blocked@example.com");
				blockedUser.setRole(userService.ROLE_BUYER);
				blockedUser.setLoginStatus(userService.LOGIN_STATUS_BLOCKED);

				Map<String, String> credentials = new HashMap<>();
				credentials.put("loginName", "blockeduser");
				credentials.put("password", "blockedpass123");

				when(userService.findByLoginName("blockeduser")).thenReturn(blockedUser);

				ResponseEntity<?> response = userController.login(credentials);

				assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
				@SuppressWarnings("unchecked")
				Map<String, String> responseBody = (Map<String, String>) response.getBody();
				assertEquals("Account is blocked", responseBody.get("error"));
			}

			@Test
			@DisplayName("Should handle invalid credentials")
			void testInvalidCredentials() {
				User user = new User();
				user.setUserId(3L);
				user.setName("Invalid Cred User");
				user.setLoginName("invaliduser");
				user.setPassword("correctpass123");
				user.setEmail("invalid@example.com");
				user.setRole(userService.ROLE_BUYER);
				user.setLoginStatus(userService.LOGIN_STATUS_ACTIVE);

				Map<String, String> credentials = new HashMap<>();
				credentials.put("loginName", "invaliduser");
				credentials.put("password", "wrongpassword");

				when(userService.findByLoginName("invaliduser")).thenReturn(user);
				when(authenticationManager.authenticate(any()))
						.thenThrow(new BadCredentialsException("Invalid credentials"));

				ResponseEntity<?> response = userController.login(credentials);

				assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
				@SuppressWarnings("unchecked")
				Map<String, String> responseBody = (Map<String, String>) response.getBody();
				assertEquals("Invalid credentials", responseBody.get("error"));
			}

			@Test
			@DisplayName("Should handle successful seller login")
			void testSellerLogin() {
				User sellerUser = new User();
				sellerUser.setUserId(4L);
				sellerUser.setName("Seller User");
				sellerUser.setLoginName("selleruser");
				sellerUser.setPassword("sellerpass123");
				sellerUser.setEmail("seller@example.com");
				sellerUser.setRole(userService.ROLE_SELLER);
				sellerUser.setLoginStatus(userService.LOGIN_STATUS_ACTIVE);

				Map<String, String> credentials = new HashMap<>();
				credentials.put("loginName", "selleruser");
				credentials.put("password", "sellerpass123");

				when(userService.findByLoginName("selleruser")).thenReturn(sellerUser);
				when(jwtService.generateToken(sellerUser)).thenReturn("seller.jwt.token");
				when(authenticationManager.authenticate(any())).thenReturn(
						new UsernamePasswordAuthenticationToken(sellerUser, null)
				);

				ResponseEntity<?> response = userController.login(credentials);

				assertEquals(HttpStatus.OK, response.getStatusCode());
				@SuppressWarnings("unchecked")
				Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
				assertNotNull(responseBody);
				assertEquals("seller.jwt.token", responseBody.get("token"));
				@SuppressWarnings("unchecked")
				Map<String, Object> userInfo = (Map<String, Object>) responseBody.get("user");
				assertEquals(sellerUser.getRole(), userInfo.get("role"));
				assertEquals(sellerUser.getUserId(), userInfo.get("userId"));
			}

			@Test
			@DisplayName("Should handle non-existent user")
			void testNonExistentUser() {
				Map<String, String> credentials = new HashMap<>();
				credentials.put("loginName", "nonexistent");
				credentials.put("password", "password123");

				when(userService.findByLoginName("nonexistent")).thenReturn(null);

				ResponseEntity<?> response = userController.login(credentials);

				assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
				@SuppressWarnings("unchecked")
				Map<String, String> responseBody = (Map<String, String>) response.getBody();
				assertEquals("Invalid credentials", responseBody.get("error"));
			}
		}

		@Nested
		@DisplayName("Status Management Tests")
		class StatusManagementTests {

			@Test
			@DisplayName("Should successfully change user status")
			void testChangeStatus() {
				Long userId = 1L;
				Integer newStatus = userService.LOGIN_STATUS_BLOCKED;

				doNothing().when(userService).changeLoginStatus(userId, newStatus);

				ResponseEntity<?> response = userController.changeStatus(userId, newStatus);

				assertEquals(HttpStatus.OK, response.getStatusCode());
				assertEquals("Status updated successfully", response.getBody());
				verify(userService).changeLoginStatus(userId, newStatus);
			}

			@Test
			@DisplayName("Should handle status change failure")
			void testChangeStatusFailure() {
				Long userId = 1L;
				Integer newStatus = userService.LOGIN_STATUS_BLOCKED;

				doThrow(new RuntimeException("Update failed"))
						.when(userService).changeLoginStatus(userId, newStatus);

				ResponseEntity<?> response = userController.changeStatus(userId, newStatus);

				assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
				assertEquals("Error updating status", response.getBody());
			}
		}

		@Test
		@DisplayName("Should successfully handle logout")
		void testLogout() {
			HttpSession mockSession = mock(HttpSession.class);

			ResponseEntity<?> response = userController.logout(mockSession);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals("Logged out successfully", response.getBody());
			verify(mockSession).invalidate();
		}

		@Test
		@DisplayName("Should check username availability")
		void testCheckUsername() {
			String username = "testuser";
			when(userService.isUsernameExist(username)).thenReturn(false);

			ResponseEntity<?> response = userController.checkUsername(username);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			@SuppressWarnings("unchecked")
			Map<String, Boolean> responseBody = (Map<String, Boolean>) response.getBody();
			assertNotNull(responseBody);
			assertTrue(responseBody.get("available"));
		}

		@Test
		@DisplayName("Should check username unavailability")
		void testCheckUsernameUnavailable() {
			String username = "existinguser";
			when(userService.isUsernameExist(username)).thenReturn(true);

			ResponseEntity<?> response = userController.checkUsername(username);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			@SuppressWarnings("unchecked")
			Map<String, Boolean> responseBody = (Map<String, Boolean>) response.getBody();
			assertNotNull(responseBody);
			assertFalse(responseBody.get("available"));
		}
	}


	@Test
	@DisplayName("Should handle internal server error during login")
	void testLoginInternalServerError() {
		Map<String, String> credentials = new HashMap<>();
		credentials.put("loginName", "testuser");
		credentials.put("password", "password123");

		when(userService.findByLoginName(anyString()))
				.thenThrow(new RuntimeException("Unexpected system error"));

		ResponseEntity<?> response = userController.login(credentials);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		@SuppressWarnings("unchecked")
		Map<String, String> responseBody = (Map<String, String>) response.getBody();
		assertTrue(responseBody.get("error").startsWith("Login failed:"));
	}

	@Test
	@DisplayName("Should handle database connection failure during login")
	void testLoginDatabaseConnectionFailure() {
		Map<String, String> credentials = new HashMap<>();
		credentials.put("loginName", "testuser");
		credentials.put("password", "password123");

		when(userService.findByLoginName(anyString()))
				.thenThrow(new IllegalStateException("Database connection failed"));

		ResponseEntity<?> response = userController.login(credentials);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		@SuppressWarnings("unchecked")
		Map<String, String> responseBody = (Map<String, String>) response.getBody();
		assertTrue(responseBody.get("error").contains("Database connection failed"));
	}

	@Test
	@DisplayName("Should handle jwt token generation failure")
	void testLoginJwtTokenGenerationFailure() {
		User activeUser = new User();
		activeUser.setUserId(1L);
		activeUser.setName("Active User");
		activeUser.setLoginName("activeuser");
		activeUser.setPassword("activepass123");
		activeUser.setEmail("active@example.com");
		activeUser.setRole(userService.ROLE_BUYER);
		activeUser.setLoginStatus(userService.LOGIN_STATUS_ACTIVE);

		Map<String, String> credentials = new HashMap<>();
		credentials.put("loginName", "activeuser");
		credentials.put("password", "activepass123");

		when(userService.findByLoginName("activeuser")).thenReturn(activeUser);
		when(authenticationManager.authenticate(any())).thenReturn(
				new UsernamePasswordAuthenticationToken(activeUser, null)
		);
		when(jwtService.generateToken(activeUser))
				.thenThrow(new RuntimeException("JWT token generation failed"));

		ResponseEntity<?> response = userController.login(credentials);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		@SuppressWarnings("unchecked")
		Map<String, String> responseBody = (Map<String, String>) response.getBody();
		assertTrue(responseBody.get("error").contains("Login failed"));
	}


}

