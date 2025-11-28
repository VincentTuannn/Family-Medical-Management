package Backend.FMM.Service;

import Backend.FMM.DTO.AuditLogDTO;
import Backend.FMM.Entity.AuditLog;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog testLog;
    private AuditLogDTO testLogDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");

        testLog = new AuditLog();
        testLog.setLogId(1);
        testLog.setUser(testUser);
        testLog.setAction("LOGIN");
        testLog.setDetails("User logged in");
        testLog.setIpAddress("192.168.1.1");
        testLog.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        testLogDTO = new AuditLogDTO();
        testLogDTO.setLogId(1);
        testLogDTO.setUserId(1);
        testLogDTO.setAction("LOGIN");
        testLogDTO.setDetails("User logged in");
        testLogDTO.setIpAddress("192.168.1.1");
    }

    @Test
    void testSave_ShouldReturnSavedLog() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            log.setLogId(1);
            return log;
        });

        // When
        AuditLogDTO result = auditLogService.save(testLogDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getLogId());
        assertEquals("LOGIN", result.getAction());
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void testFindById_WhenExists_ShouldReturnLog() {
        // Given
        when(auditLogRepository.findById(1)).thenReturn(Optional.of(testLog));

        // When
        Optional<AuditLogDTO> result = auditLogService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getLogId());
        assertEquals("LOGIN", result.get().getAction());
        verify(auditLogRepository).findById(1);
    }

    @Test
    void testFindById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(auditLogRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<AuditLogDTO> result = auditLogService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(auditLogRepository).findById(999);
    }

    @Test
    void testFindAllByUserId_ShouldReturnList() {
        // Given
        List<AuditLog> logs = Arrays.asList(testLog);
        when(auditLogRepository.findByUser_UserId(1)).thenReturn(logs);

        // When
        List<AuditLogDTO> result = auditLogService.findAllByUserId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository).findByUser_UserId(1);
    }

    @Test
    void testFindAll_ShouldReturnList() {
        // Given
        List<AuditLog> logs = Arrays.asList(testLog);
        when(auditLogRepository.findAll()).thenReturn(logs);

        // When
        List<AuditLogDTO> result = auditLogService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository).findAll();
    }

    @Test
    void testDeleteById_ShouldCallRepository() {
        // When
        auditLogService.deleteById(1);

        // Then
        verify(auditLogRepository).deleteById(1);
    }

    @Test
    void testLogAction_ShouldSaveLog() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            log.setLogId(1);
            return log;
        });

        // When
        auditLogService.logAction(1, "LOGIN", "User logged in", "192.168.1.1");

        // Then
        verify(auditLogRepository).save(any(AuditLog.class));
    }
}


