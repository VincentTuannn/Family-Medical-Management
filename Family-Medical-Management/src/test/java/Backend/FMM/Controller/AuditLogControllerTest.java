package Backend.FMM.Controller;

import Backend.FMM.DTO.AuditLogDTO;
import Backend.FMM.Service.AuditLogService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    private AuditLogDTO testAuditLogDTO;

    @BeforeEach
    void setUp() {
        testAuditLogDTO = new AuditLogDTO();
        testAuditLogDTO.setLogId(1);
        testAuditLogDTO.setUserId(1);
        testAuditLogDTO.setAction("CREATE");
        testAuditLogDTO.setDetails("Created user");
        testAuditLogDTO.setIpAddress("127.0.0.1");
        testAuditLogDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void testGetAllAuditLogs_ShouldReturnList() {
        // Given
        AuditLogDTO log2 = new AuditLogDTO();
        log2.setLogId(2);
        List<AuditLogDTO> logs = Arrays.asList(testAuditLogDTO, log2);
        when(auditLogService.findAll()).thenReturn(logs);

        // When
        List<AuditLogDTO> result = auditLogController.getAllAuditLogs();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(auditLogService).findAll();
    }

    @Test
    void testGetAuditLogById_WhenExists_ShouldReturn200() {
        // Given
        when(auditLogService.findById(1)).thenReturn(Optional.of(testAuditLogDTO));

        // When
        ResponseEntity<AuditLogDTO> response = auditLogController.getAuditLogById(1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getLogId());
        verify(auditLogService).findById(1);
    }

    @Test
    void testGetAuditLogById_WhenNotExists_ShouldReturn404() {
        // Given
        when(auditLogService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<AuditLogDTO> response = auditLogController.getAuditLogById(999);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(auditLogService).findById(999);
    }

    @Test
    void testGetAuditLogsByUserId_ShouldReturnList() {
        // Given
        AuditLogDTO log2 = new AuditLogDTO();
        log2.setLogId(2);
        log2.setUserId(1);
        List<AuditLogDTO> logs = Arrays.asList(testAuditLogDTO, log2);
        when(auditLogService.findAllByUserId(1)).thenReturn(logs);

        // When
        List<AuditLogDTO> result = auditLogController.getAuditLogsByUserId(1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(auditLogService).findAllByUserId(1);
    }

    @Test
    void testCreateAuditLog_ShouldReturnAuditLogDTO() {
        // Given
        when(auditLogService.save(any(AuditLogDTO.class))).thenReturn(testAuditLogDTO);

        // When
        AuditLogDTO result = auditLogController.createAuditLog(testAuditLogDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getLogId());
        verify(auditLogService).save(testAuditLogDTO);
    }

    @Test
    void testDeleteAuditLog_ShouldReturn204() {
        // Given
        doNothing().when(auditLogService).deleteById(1);

        // When
        ResponseEntity<Void> response = auditLogController.deleteAuditLog(1);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(auditLogService).deleteById(1);
    }
}

