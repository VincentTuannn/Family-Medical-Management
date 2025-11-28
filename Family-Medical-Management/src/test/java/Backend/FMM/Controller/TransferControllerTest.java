package Backend.FMM.Controller;

import Backend.FMM.DTO.TransferDTO;
import Backend.FMM.Service.TransferService;
import Backend.FMM.Security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private TransferController transferController;

    private TransferDTO testTransferDTO;

    @BeforeEach
    void setUp() {
        testTransferDTO = new TransferDTO();
        testTransferDTO.setTransferId(1);
        testTransferDTO.setUserId(1);
        testTransferDTO.setPatientId(1);
        testTransferDTO.setAccessType("VIEW");
        testTransferDTO.setStatus("PENDING");
        testTransferDTO.setExpiresAt(new Date(System.currentTimeMillis() + 86400000));
    }

    @Test
    void testGetAllTransfers_ShouldReturnList() {
        // Given
        TransferDTO transfer2 = new TransferDTO();
        transfer2.setTransferId(2);
        List<TransferDTO> transfers = Arrays.asList(testTransferDTO, transfer2);
        when(transferService.findAll()).thenReturn(transfers);

        // When
        List<TransferDTO> result = transferController.getAllTransfers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transferService).findAll();
    }

    @Test
    void testGetTransferById_WhenExists_ShouldReturn200() {
        // Given
        when(transferService.findById(1)).thenReturn(Optional.of(testTransferDTO));

        // When
        ResponseEntity<TransferDTO> response = transferController.getTransferById(1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTransferId());
        verify(transferService).findById(1);
    }

    @Test
    void testGetTransferById_WhenNotExists_ShouldReturn404() {
        // Given
        when(transferService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<TransferDTO> response = transferController.getTransferById(999);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(transferService).findById(999);
    }

    @Test
    void testGetTransfersByUserId_ShouldReturnList() {
        // Given
        TransferDTO transfer2 = new TransferDTO();
        transfer2.setTransferId(2);
        transfer2.setUserId(1);
        List<TransferDTO> transfers = Arrays.asList(testTransferDTO, transfer2);
        when(transferService.findAllByUserId(1)).thenReturn(transfers);

        // When
        List<TransferDTO> result = transferController.getTransfersByUserId(1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transferService).findAllByUserId(1);
    }

    @Test
    void testGetMyTransfers_WithValidToken_ShouldReturnList() {
        // Given
        Integer userId = 1;
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(transferService.findAllByUserId(userId)).thenReturn(Arrays.asList(testTransferDTO));

        // When
        List<TransferDTO> result = transferController.getMyTransfers(httpRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jwtTokenProvider).getUserIdFromJWT("test-token");
        verify(transferService).findAllByUserId(userId);
    }

    @Test
    void testGetMyTransfers_WithNoAuthHeader_ShouldReturnEmptyList() {
        // Given
        when(httpRequest.getHeader("Authorization")).thenReturn(null);

        // When
        List<TransferDTO> result = transferController.getMyTransfers(httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jwtTokenProvider, never()).getUserIdFromJWT(anyString());
    }

    @Test
    void testGetMyTransfers_WithInvalidToken_ShouldReturnEmptyList() {
        // Given
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtTokenProvider.getUserIdFromJWT("invalid-token")).thenReturn(null);

        // When
        List<TransferDTO> result = transferController.getMyTransfers(httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jwtTokenProvider).getUserIdFromJWT("invalid-token");
        verify(transferService, never()).findAllByUserId(anyInt());
    }

    @Test
    void testCreateTransfer_ShouldReturnTransferDTO() {
        // Given
        when(transferService.save(any(TransferDTO.class))).thenReturn(testTransferDTO);

        // When
        TransferDTO result = transferController.createTransfer(testTransferDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTransferId());
        verify(transferService).save(testTransferDTO);
    }

    @Test
    void testUpdateTransfer_WhenExists_ShouldReturn200() {
        // Given
        testTransferDTO.setStatus("APPROVED");
        when(transferService.findById(1)).thenReturn(Optional.of(testTransferDTO));
        when(transferService.save(any(TransferDTO.class))).thenReturn(testTransferDTO);

        // When
        ResponseEntity<TransferDTO> response = transferController.updateTransfer(1, testTransferDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTransferId());
        verify(transferService).findById(1);
        verify(transferService).save(argThat(dto -> dto.getTransferId() == 1));
    }

    @Test
    void testUpdateTransfer_WhenNotExists_ShouldReturn404() {
        // Given
        when(transferService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<TransferDTO> response = transferController.updateTransfer(999, testTransferDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(transferService).findById(999);
        verify(transferService, never()).save(any());
    }

    @Test
    void testDeleteTransfer_ShouldReturn204() {
        // Given
        doNothing().when(transferService).deleteById(1);

        // When
        ResponseEntity<Void> response = transferController.deleteTransfer(1);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transferService).deleteById(1);
    }
}

