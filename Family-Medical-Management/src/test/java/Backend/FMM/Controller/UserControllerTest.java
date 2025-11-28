package Backend.FMM.Controller;

import Backend.FMM.DTO.UserDTO;
import Backend.FMM.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setUserId(1);
        testUserDTO.setUsername("testuser");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setPassword("password");
        testUserDTO.setPhone("123456789");
        testUserDTO.setAddress("Test Address");
        testUserDTO.setRole("USER");
        testUserDTO.setIsActive(true);
        testUserDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void testGetAllUsers_ShouldReturnList() {
        // Given
        UserDTO user2 = new UserDTO();
        user2.setUserId(2);
        user2.setUsername("user2");
        List<UserDTO> users = Arrays.asList(testUserDTO, user2);
        when(userService.findAll()).thenReturn(users);

        // When
        List<UserDTO> result = userController.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userService).findAll();
    }

    @Test
    void testGetUserById_WhenUserExists_ShouldReturn200() {
        // Given
        when(userService.findById(1)).thenReturn(Optional.of(testUserDTO));

        // When
        ResponseEntity<UserDTO> response = userController.getUserById(1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUserDTO.getUserId(), response.getBody().getUserId());
        verify(userService).findById(1);
    }

    @Test
    void testGetUserById_WhenUserNotExists_ShouldReturn404() {
        // Given
        when(userService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<UserDTO> response = userController.getUserById(999);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).findById(999);
    }

    @Test
    void testCreateUser_ShouldReturnUserDTO() {
        // Given
        when(userService.save(any(UserDTO.class))).thenReturn(testUserDTO);

        // When
        UserDTO result = userController.createUser(testUserDTO);

        // Then
        assertNotNull(result);
        assertEquals(testUserDTO.getUserId(), result.getUserId());
        verify(userService).save(testUserDTO);
    }

    @Test
    void testUpdateUser_WhenUserExists_ShouldReturn200() {
        // Given
        testUserDTO.setUsername("updateduser");
        when(userService.findById(1)).thenReturn(Optional.of(testUserDTO));
        when(userService.save(any(UserDTO.class))).thenReturn(testUserDTO);

        // When
        ResponseEntity<UserDTO> response = userController.updateUser(1, testUserDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getUserId());
        verify(userService).findById(1);
        verify(userService).save(argThat(dto -> dto.getUserId() == 1));
    }

    @Test
    void testUpdateUser_WhenUserNotExists_ShouldReturn404() {
        // Given
        when(userService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<UserDTO> response = userController.updateUser(999, testUserDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).findById(999);
        verify(userService, never()).save(any());
    }

    @Test
    void testDeleteUser_ShouldReturn204() {
        // Given
        doNothing().when(userService).deleteById(1);

        // When
        ResponseEntity<Void> response = userController.deleteUser(1);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteById(1);
    }

    @Test
    void testTestDB_ShouldReturnOK() {
        // Given
        doNothing().when(userService).testDB();

        // When
        String result = userController.test();

        // Then
        assertEquals("OK", result);
        verify(userService).testDB();
    }
}


