package Backend.FMM.Controller;

import Backend.FMM.DTO.PatientDTO;
import Backend.FMM.Service.PatientService;
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
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private PatientController patientController;

    private PatientDTO testPatientDTO;

    @BeforeEach
    void setUp() {
        testPatientDTO = new PatientDTO();
        testPatientDTO.setPatientId(1);
        testPatientDTO.setUserId(1);
        testPatientDTO.setFullName("Test Patient");
        testPatientDTO.setDateOfBirth(new Date(System.currentTimeMillis()));
        testPatientDTO.setGender("MALE");
        testPatientDTO.setEmergencyContact("123456789");
    }

    @Test
    void testGetAllPatients_ShouldReturnList() {
        // Given
        PatientDTO patient2 = new PatientDTO();
        patient2.setPatientId(2);
        List<PatientDTO> patients = Arrays.asList(testPatientDTO, patient2);
        when(patientService.findAll()).thenReturn(patients);

        // When
        List<PatientDTO> result = patientController.getAllPatients();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(patientService).findAll();
    }

    @Test
    void testGetPatientById_WhenExists_ShouldReturn200() {
        // Given
        when(patientService.findById(1)).thenReturn(Optional.of(testPatientDTO));

        // When
        ResponseEntity<PatientDTO> response = patientController.getPatientById(1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getPatientId());
        verify(patientService).findById(1);
    }

    @Test
    void testGetPatientById_WhenNotExists_ShouldReturn404() {
        // Given
        when(patientService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<PatientDTO> response = patientController.getPatientById(999);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(patientService).findById(999);
    }

    @Test
    void testGetPatientsByUserId_ShouldReturnList() {
        // Given
        PatientDTO patient2 = new PatientDTO();
        patient2.setPatientId(2);
        patient2.setUserId(1);
        List<PatientDTO> patients = Arrays.asList(testPatientDTO, patient2);
        when(patientService.findAllByUserId(1)).thenReturn(patients);

        // When
        List<PatientDTO> result = patientController.getPatientsByUserId(1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(patientService).findAllByUserId(1);
    }

    @Test
    void testGetMyPatients_WithValidToken_ShouldReturnList() {
        // Given
        Integer userId = 1;
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(patientService.findAllByUserId(userId)).thenReturn(Arrays.asList(testPatientDTO));

        // When
        List<PatientDTO> result = patientController.getMyPatients(httpRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jwtTokenProvider).getUserIdFromJWT("test-token");
        verify(patientService).findAllByUserId(userId);
    }

    @Test
    void testGetMyPatients_WithNoAuthHeader_ShouldReturnEmptyList() {
        // Given
        when(httpRequest.getHeader("Authorization")).thenReturn(null);

        // When
        List<PatientDTO> result = patientController.getMyPatients(httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jwtTokenProvider, never()).getUserIdFromJWT(anyString());
    }

    @Test
    void testGetMyPatients_WithInvalidToken_ShouldReturnEmptyList() {
        // Given
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtTokenProvider.getUserIdFromJWT("invalid-token")).thenReturn(null);

        // When
        List<PatientDTO> result = patientController.getMyPatients(httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jwtTokenProvider).getUserIdFromJWT("invalid-token");
        verify(patientService, never()).findAllByUserId(anyInt());
    }

    @Test
    void testCreatePatient_ShouldReturnPatientDTO() {
        // Given
        when(patientService.save(any(PatientDTO.class))).thenReturn(testPatientDTO);

        // When
        PatientDTO result = patientController.createPatient(testPatientDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPatientId());
        verify(patientService).save(testPatientDTO);
    }

    @Test
    void testUpdatePatient_WhenExists_ShouldReturn200() {
        // Given
        testPatientDTO.setFullName("Updated Patient");
        when(patientService.findById(1)).thenReturn(Optional.of(testPatientDTO));
        when(patientService.save(any(PatientDTO.class))).thenReturn(testPatientDTO);

        // When
        ResponseEntity<PatientDTO> response = patientController.updatePatient(1, testPatientDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getPatientId());
        verify(patientService).findById(1);
        verify(patientService).save(argThat(dto -> dto.getPatientId() == 1));
    }

    @Test
    void testUpdatePatient_WhenNotExists_ShouldReturn404() {
        // Given
        when(patientService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<PatientDTO> response = patientController.updatePatient(999, testPatientDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(patientService).findById(999);
        verify(patientService, never()).save(any());
    }

    @Test
    void testDeletePatient_ShouldReturn204() {
        // Given
        doNothing().when(patientService).deleteById(1);

        // When
        ResponseEntity<Void> response = patientController.deletePatient(1);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(patientService).deleteById(1);
    }
}

