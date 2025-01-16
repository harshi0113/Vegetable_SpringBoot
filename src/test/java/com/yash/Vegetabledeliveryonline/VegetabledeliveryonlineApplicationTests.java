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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import javax.naming.AuthenticationException;
import java.time.Duration;
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
	@DisplayName("User Controller Tests")
	class UserControllerTests {


		//Test user registration with image upload
		@Test
		@DisplayName("Should register user with image")
		void testRegisterWithImage() throws Exception {


			// Create test image file
			MockMultipartFile imageFile = new MockMultipartFile(
					"imageFile",
					"test.jpg",
					MediaType.IMAGE_JPEG_VALUE,
					"test image content".getBytes()
			);

			// Mock service behavior
			when(userService.register(any(User.class))).thenAnswer(invocation -> {
				User registeredUser = invocation.getArgument(0);
				registeredUser.setUserId(1L); // Simulate DB save
				return registeredUser;
			});

			// Execute test
			ResponseEntity<?> response = userController.register(testUser, imageFile);

			// Verify response
			assertEquals(HttpStatus.OK, response.getStatusCode());
			verify(userService).register(argThat(registeredUser ->
					registeredUser.getLoginStatus().equals(UserService.LOGIN_STATUS_ACTIVE) &&
							registeredUser.getImage() != null
			));
		}


		//Test various login scenarios (success, blocked user, auth failure)
		@Test
		@DisplayName("Should handle login with different scenarios")
		void testLoginScenarios() {
			// Setup test data
			Map<String, String> credentials = new HashMap<>();
			credentials.put("loginName", "testuser");
			credentials.put("password", "password123");

			User activeUser = new User();
			activeUser.setUserId(1L);
			activeUser.setName("Test User");
			activeUser.setLoginName("testuser");
			activeUser.setPassword("encoded_password");
			activeUser.setEmail("test@example.com");
			activeUser.setLoginStatus(UserService.LOGIN_STATUS_ACTIVE);

			// Scenario 1: Successful login
			when(userService.findByLoginName("testuser")).thenReturn(activeUser);
			when(jwtService.generateToken(activeUser)).thenReturn("test.jwt.token");
			when(authenticationManager.authenticate(any())).thenReturn(null);

			ResponseEntity<?> response = userController.login(credentials);
			assertEquals(HttpStatus.OK, response.getStatusCode());
			verify(jwtService).generateToken(activeUser);

			// Scenario 2: Blocked user
			User blockedUser = new User();
			blockedUser.setLoginName("testuser");
			blockedUser.setPassword("encoded_password");
			blockedUser.setLoginStatus(UserService.LOGIN_STATUS_BLOCKED);

			when(userService.findByLoginName("testuser")).thenReturn(blockedUser);

			response = userController.login(credentials);
			assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

			// Scenario 3: Authentication failure
			when(userService.findByLoginName("testuser")).thenReturn(activeUser);
			when(authenticationManager.authenticate(any()))
					.thenThrow(new AuthenticationException("Invalid credentials") {});

			response = userController.login(credentials);
			assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		}


		//Test username availability check
		@Test
		@DisplayName("Should check username availability")
		void testCheckUsername() {
			// Given
			String username = "testuser";
			when(userService.isUsernameExist(username)).thenReturn(false);

			// When
			ResponseEntity<?> response = userController.checkUsername(username);

			// Then
			assertEquals(HttpStatus.OK, response.getStatusCode());
			@SuppressWarnings("unchecked")
			Map<String, Boolean> responseBody = (Map<String, Boolean>) response.getBody();
			assertNotNull(responseBody);
			assertTrue(responseBody.get("available"));
		}


		//Test logout functionality
		@Test
		@DisplayName("Should handle logout")
		void testLogout() {
			// Given
			HttpSession mockSession = mock(HttpSession.class);

			// When
			ResponseEntity<?> response = userController.logout(mockSession);

			// Then
			assertEquals(HttpStatus.OK, response.getStatusCode());
			verify(mockSession).invalidate();
		}
	}

		@Test
		@DisplayName("Should demonstrate timeout")
		void testTimeout() {
			// Demonstrates timeout testing
			assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
				userService.getTotalUsers();
			});
		}


	}