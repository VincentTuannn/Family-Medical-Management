package Backend.FMM.Controller;

import Backend.FMM.DTO.AppointmentDTO;
import Backend.FMM.DTO.PatientDTO;
import Backend.FMM.Service.AppointmentService;
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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private PatientService patientService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private AppointmentController appointmentController;

    private AppointmentDTO testAppointmentDTO;
    private PatientDTO testPatientDTO;

    @BeforeEach
    void setUp() {
        testAppointmentDTO = new AppointmentDTO();
        testAppointmentDTO.setAppointmentId(1);
        testAppointmentDTO.setPatientId(1);
        testAppointmentDTO.setAppointmentDate(new Timestamp(System.currentTimeMillis()));
        testAppointmentDTO.setStatus("SCHEDULED");
        testAppointmentDTO.setNotes("Test appointment");

        testPatientDTO = new PatientDTO();
        testPatientDTO.setPatientId(1);
        testPatientDTO.setUserId(1);
        testPatientDTO.setFullName("Test Patient");
    }

    @Test
    void testGetAllAppointments_ShouldReturnList() {
        // Given
        AppointmentDTO appointment2 = new AppointmentDTO();
        appointment2.setAppointmentId(2);
        List<AppointmentDTO> appointments = Arrays.asList(testAppointmentDTO, appointment2);
        when(appointmentService.findAll()).thenReturn(appointments);

        // When
        List<AppointmentDTO> result = appointmentController.getAllAppointments();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentService).findAll();
    }

    @Test
    void testGetAppointmentById_WhenExists_ShouldReturn200() {
        // Given
        when(appointmentService.findById(1)).thenReturn(Optional.of(testAppointmentDTO));

        // When
        ResponseEntity<AppointmentDTO> response = appointmentController.getAppointmentById(1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getAppointmentId());
        verify(appointmentService).findById(1);
    }

    @Test
    void testGetAppointmentById_WhenNotExists_ShouldReturn404() {
        // Given
        when(appointmentService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<AppointmentDTO> response = appointmentController.getAppointmentById(999);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).findById(999);
    }

    @Test
    void testGetAppointmentsByPatientId_ShouldReturnList() {
        // Given
        AppointmentDTO appointment2 = new AppointmentDTO();
        appointment2.setAppointmentId(2);
        appointment2.setPatientId(1);
        List<AppointmentDTO> appointments = Arrays.asList(testAppointmentDTO, appointment2);
        when(appointmentService.findAllByPatientId(1)).thenReturn(appointments);

        // When
        List<AppointmentDTO> result = appointmentController.getAppointmentsByPatientId(1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(appointmentService).findAllByPatientId(1);
    }

    @Test
    void testGetMyAppointments_WithValidToken_ShouldReturnList() {
        // Given
        Integer userId = 1;
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtTokenProvider.getUserIdFromJWT("test-token")).thenReturn(userId);
        when(patientService.findAllByUserId(userId)).thenReturn(Arrays.asList(testPatientDTO));
        when(appointmentService.findAllByPatientId(1)).thenReturn(Arrays.asList(testAppointmentDTO));

        // When
        List<AppointmentDTO> result = appointmentController.getMyAppointments(httpRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jwtTokenProvider).getUserIdFromJWT("test-token");
        verify(patientService).findAllByUserId(userId);
        verify(appointmentService).findAllByPatientId(1);
    }

    @Test
    void testGetMyAppointments_WithNoAuthHeader_ShouldReturnEmptyList() {
        // Given
        when(httpRequest.getHeader("Authorization")).thenReturn(null);

        // When
        List<AppointmentDTO> result = appointmentController.getMyAppointments(httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jwtTokenProvider, never()).getUserIdFromJWT(anyString());
    }

    @Test
    void testGetMyAppointments_WithInvalidToken_ShouldReturnEmptyList() {
        // Given
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtTokenProvider.getUserIdFromJWT("invalid-token")).thenReturn(null);

        // When
        List<AppointmentDTO> result = appointmentController.getMyAppointments(httpRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jwtTokenProvider).getUserIdFromJWT("invalid-token");
        verify(patientService, never()).findAllByUserId(anyInt());
    }

    @Test
    void testCreateAppointment_ShouldReturnAppointmentDTO() {
        // Given
        when(appointmentService.save(any(AppointmentDTO.class))).thenReturn(testAppointmentDTO);

        // When
        AppointmentDTO result = appointmentController.createAppointment(testAppointmentDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAppointmentId());
        verify(appointmentService).save(testAppointmentDTO);
    }

    @Test
    void testUpdateAppointment_WhenExists_ShouldReturn200() {
        // Given
        testAppointmentDTO.setNotes("Updated notes");
        when(appointmentService.findById(1)).thenReturn(Optional.of(testAppointmentDTO));
        when(appointmentService.save(any(AppointmentDTO.class))).thenReturn(testAppointmentDTO);

        // When
        ResponseEntity<AppointmentDTO> response = appointmentController.updateAppointment(1, testAppointmentDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getAppointmentId());
        verify(appointmentService).findById(1);
        verify(appointmentService).save(argThat(dto -> dto.getAppointmentId() == 1));
    }

    @Test
    void testUpdateAppointment_WhenNotExists_ShouldReturn404() {
        // Given
        when(appointmentService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<AppointmentDTO> response = appointmentController.updateAppointment(999, testAppointmentDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).findById(999);
        verify(appointmentService, never()).save(any());
    }

    @Test
    void testDeleteAppointment_ShouldReturn204() {
        // Given
        doNothing().when(appointmentService).deleteById(1);

        // When
        ResponseEntity<Void> response = appointmentController.deleteAppointment(1);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(appointmentService).deleteById(1);
    }
}

