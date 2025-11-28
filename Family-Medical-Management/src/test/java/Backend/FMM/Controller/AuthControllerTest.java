package Backend.FMM.Controller;

import Backend.FMM.DTO.UserDTO;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.UserRepository;
import Backend.FMM.Service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private UserDTO loginDTO;
    private UserDTO registerDTO;

    @BeforeEach
    void setUp() {
        loginDTO = new UserDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password");

        registerDTO = new UserDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setEmail("newuser@example.com");
        registerDTO.setPassword("password");
        registerDTO.setPhone("123456789");
        registerDTO.setAddress("Test Address");
    }

    @Test
    void testLogin_WithValidCredentials_ShouldReturn200WithToken() {
        // Given
        when(authService.authenticate("testuser", "password")).thenReturn("test-token");

        // When
        ResponseEntity<?> response = authController.login(loginDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("test-token", body.get("token"));
        assertEquals("Login thành công", body.get("message"));
        verify(authService).authenticate("testuser", "password");
    }

    @Test
    void testLogin_WithInvalidCredentials_ShouldReturn400() {
        // Given
        when(authService.authenticate("testuser", "wrongpassword"))
                .thenThrow(new BadCredentialsException("Sai mật khẩu"));

        // When
        ResponseEntity<?> response = authController.login(loginDTO);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertTrue(body.containsKey("error"));
        verify(authService).authenticate("testuser", "password");
    }

    @Test
    void testRegister_WithValidData_ShouldReturn200() {
        // Given
        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com");
        when(passwordEncoder.encode("password")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        ResponseEntity<?> response = authController.register(registerDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof User);
        User result = (User) response.getBody();
        assertEquals("newuser", result.getUsername());
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("newuser") &&
                user.getEmail().equals("newuser@example.com") &&
                user.getRole() == User.Role.USER &&
                user.isActive()
        ));
    }

    @Test
    void testRegister_WithException_ShouldReturn400() {
        // Given
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<?> response = authController.register(registerDTO);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertTrue(body.containsKey("error"));
    }
}


