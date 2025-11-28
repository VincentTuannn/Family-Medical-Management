package Backend.FMM.Service;

import Backend.FMM.DTO.UserDTO;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPhone("123456789");
        testUser.setAddress("Test Address");
        testUser.setRole(User.Role.USER);
        testUser.setActive(true);
        testUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        testUserDTO = new UserDTO();
        testUserDTO.setUserId(1);
        testUserDTO.setUsername("testuser");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setPassword("plainPassword");
        testUserDTO.setPhone("123456789");
        testUserDTO.setAddress("Test Address");
        testUserDTO.setRole("USER");
        testUserDTO.setIsActive(true);
    }

    @Test
    void testSave_ShouldEncodePassword() {
        // Given
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.save(testUserDTO);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testSave_WithNullRole_ShouldSetDefaultRole() {
        // Given
        testUserDTO.setRole(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1);
            return user;
        });

        // When
        UserDTO result = userService.save(testUserDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getRole() == User.Role.USER));
    }

    @Test
    void testSave_WithInvalidRole_ShouldSetDefaultRole() {
        // Given
        testUserDTO.setRole("INVALID_ROLE");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1);
            return user;
        });

        // When
        UserDTO result = userService.save(testUserDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getRole() == User.Role.USER));
    }

    @Test
    void testSave_WithLowerCaseRole_ShouldConvertToUpperCase() {
        // Given
        testUserDTO.setRole("admin");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1);
            return user;
        });

        // When
        UserDTO result = userService.save(testUserDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).save(argThat(user -> user.getRole() == User.Role.ADMIN));
    }

    @Test
    void testFindById_WhenUserExists_ShouldReturnUserDTO() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUserId(), result.get().getUserId());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository).findById(1);
    }

    @Test
    void testFindById_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(999);
    }

    @Test
    void testFindAll_ShouldReturnListOfUserDTOs() {
        // Given
        User user2 = new User();
        user2.setUserId(2);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setRole(User.Role.USER);
        user2.setActive(true);

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserDTO> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getUserId(), result.get(0).getUserId());
        assertEquals(user2.getUserId(), result.get(1).getUserId());
        verify(userRepository).findAll();
    }

    @Test
    void testDeleteById_ShouldCallRepository() {
        // Given
        doNothing().when(userRepository).deleteById(1);

        // When
        userService.deleteById(1);

        // Then
        verify(userRepository).deleteById(1);
    }

    @Test
    void testFindByEmail_WhenUserExists_ShouldReturnUserDTO() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDTO> result = userService.findByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testFindByEmail_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.findByEmail("notfound@example.com");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("notfound@example.com");
    }

    @Test
    void testTestDB_ShouldCallRepository() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        // When
        userService.testDB();

        // Then
        verify(userRepository).findAll();
    }
}


