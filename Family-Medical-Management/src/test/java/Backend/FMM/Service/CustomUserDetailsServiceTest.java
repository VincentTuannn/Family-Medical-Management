package Backend.FMM.Service;

import Backend.FMM.Entity.User;
import Backend.FMM.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);
        testUser.setActive(true);
        testUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void testLoadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_WhenUserNotActive_ShouldReturnDisabledUser() {
        // Given
        testUser.setActive(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertFalse(result.isEnabled());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void testLoadUserByUsername_WhenUserIsAdmin_ShouldReturnAdminRole() {
        // Given
        testUser.setRole(User.Role.ADMIN);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        // When
        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        // Then
        assertNotNull(result);
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository).findByUsername("admin");
    }
}


