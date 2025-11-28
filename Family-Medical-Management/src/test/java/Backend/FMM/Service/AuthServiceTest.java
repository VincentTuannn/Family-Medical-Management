package Backend.FMM.Service;

import Backend.FMM.Entity.User;
import Backend.FMM.Repository.UserRepository;
import Backend.FMM.Security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setActive(true);

        authentication = mock(Authentication.class);
    }

    @Test
    void testAuthenticate_WithValidCredentials_ShouldReturnToken() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication, 1)).thenReturn("test-token");

        // When
        String token = authService.authenticate("testuser", "password");

        // Then
        assertNotNull(token);
        assertEquals("test-token", token);
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password", testUser.getPassword());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authentication, 1);
    }

    @Test
    void testAuthenticate_WhenUserNotExists_ShouldThrowException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            authService.authenticate("nonexistent", "password");
        });
        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void testAuthenticate_WithWrongPassword_ShouldThrowBadCredentialsException() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate("testuser", "wrongpassword");
        });
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongpassword", testUser.getPassword());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void testAuthenticate_WithInactiveUser_ShouldThrowDisabledException() {
        // Given
        testUser.setActive(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);

        // When & Then
        assertThrows(DisabledException.class, () -> {
            authService.authenticate("testuser", "password");
        });
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password", testUser.getPassword());
        verify(authenticationManager, never()).authenticate(any());
    }
}


