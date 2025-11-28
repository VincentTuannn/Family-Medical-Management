package Backend.FMM.Controller;

import Backend.FMM.DTO.DoctorDTO;
import Backend.FMM.Service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    private DoctorDTO testDoctorDTO;

    @BeforeEach
    void setUp() {
        testDoctorDTO = new DoctorDTO();
        testDoctorDTO.setDoctorId(1);
        testDoctorDTO.setFullName("Dr. John Doe");
        testDoctorDTO.setSpecialty("Cardiology");
        testDoctorDTO.setClinicName("Heart Clinic");
        testDoctorDTO.setEmail("john@example.com");
        testDoctorDTO.setPhone("123456789");
        testDoctorDTO.setLicenseNumber("LIC123");
        testDoctorDTO.setIsActive(true);
    }

    @Test
    void testGetAllDoctors_ShouldReturnList() {
        // Given
        DoctorDTO doctor2 = new DoctorDTO();
        doctor2.setDoctorId(2);
        List<DoctorDTO> doctors = Arrays.asList(testDoctorDTO, doctor2);
        when(doctorService.findAll()).thenReturn(doctors);

        // When
        List<DoctorDTO> result = doctorController.getAllDoctors();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(doctorService).findAll();
    }

    @Test
    void testGetDoctorById_WhenDoctorExists_ShouldReturn200() {
        // Given
        when(doctorService.findById(1)).thenReturn(Optional.of(testDoctorDTO));

        // When
        ResponseEntity<DoctorDTO> response = doctorController.getDoctorById(1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testDoctorDTO.getDoctorId(), response.getBody().getDoctorId());
        verify(doctorService).findById(1);
    }

    @Test
    void testGetDoctorById_WhenDoctorNotExists_ShouldReturn404() {
        // Given
        when(doctorService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<DoctorDTO> response = doctorController.getDoctorById(999);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(doctorService).findById(999);
    }

    @Test
    void testCreateDoctor_ShouldReturnDoctorDTO() {
        // Given
        when(doctorService.save(any(DoctorDTO.class))).thenReturn(testDoctorDTO);

        // When
        DoctorDTO result = doctorController.createDoctor(testDoctorDTO);

        // Then
        assertNotNull(result);
        assertEquals(testDoctorDTO.getDoctorId(), result.getDoctorId());
        verify(doctorService).save(testDoctorDTO);
    }

    @Test
    void testUpdateDoctor_WhenDoctorExists_ShouldReturn200() {
        // Given
        testDoctorDTO.setFullName("Dr. John Updated");
        when(doctorService.findById(1)).thenReturn(Optional.of(testDoctorDTO));
        when(doctorService.save(any(DoctorDTO.class))).thenReturn(testDoctorDTO);

        // When
        ResponseEntity<DoctorDTO> response = doctorController.updateDoctor(1, testDoctorDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getDoctorId());
        verify(doctorService).findById(1);
        verify(doctorService).save(argThat(dto -> dto.getDoctorId() == 1));
    }

    @Test
    void testUpdateDoctor_WhenDoctorNotExists_ShouldReturn404() {
        // Given
        when(doctorService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<DoctorDTO> response = doctorController.updateDoctor(999, testDoctorDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(doctorService).findById(999);
        verify(doctorService, never()).save(any());
    }

    @Test
    void testDeleteDoctor_ShouldReturn204() {
        // Given
        doNothing().when(doctorService).deleteById(1);

        // When
        ResponseEntity<Void> response = doctorController.deleteDoctor(1);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(doctorService).deleteById(1);
    }
}


