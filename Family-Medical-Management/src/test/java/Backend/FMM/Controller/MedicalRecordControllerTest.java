package Backend.FMM.Controller;

import Backend.FMM.DTO.MedicalRecordDTO;
import Backend.FMM.Service.MedicalRecordService;
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
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    private MedicalRecordDTO testMedicalRecordDTO;

    @BeforeEach
    void setUp() {
        testMedicalRecordDTO = new MedicalRecordDTO();
        testMedicalRecordDTO.setRecordId(1);
        testMedicalRecordDTO.setPatientId(1);
        testMedicalRecordDTO.setDiagnosis("Test diagnosis");
        testMedicalRecordDTO.setTreatment("Test treatment");
        testMedicalRecordDTO.setRecordDate(new Date(System.currentTimeMillis()));
    }

    @Test
    void testGetAllMedicalRecords_ShouldReturnList() {
        // Given
        MedicalRecordDTO record2 = new MedicalRecordDTO();
        record2.setRecordId(2);
        List<MedicalRecordDTO> records = Arrays.asList(testMedicalRecordDTO, record2);
        when(medicalRecordService.findAll()).thenReturn(records);

        // When
        List<MedicalRecordDTO> result = medicalRecordController.getAllMedicalRecords();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(medicalRecordService).findAll();
    }

    @Test
    void testGetMedicalRecordById_WhenExists_ShouldReturn200() {
        // Given
        when(medicalRecordService.findById(1)).thenReturn(Optional.of(testMedicalRecordDTO));

        // When
        ResponseEntity<MedicalRecordDTO> response = medicalRecordController.getMedicalRecordById(1);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getRecordId());
        verify(medicalRecordService).findById(1);
    }

    @Test
    void testGetMedicalRecordById_WhenNotExists_ShouldReturn404() {
        // Given
        when(medicalRecordService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<MedicalRecordDTO> response = medicalRecordController.getMedicalRecordById(999);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(medicalRecordService).findById(999);
    }

    @Test
    void testGetMedicalRecordsByPatientId_ShouldReturnList() {
        // Given
        MedicalRecordDTO record2 = new MedicalRecordDTO();
        record2.setRecordId(2);
        record2.setPatientId(1);
        List<MedicalRecordDTO> records = Arrays.asList(testMedicalRecordDTO, record2);
        when(medicalRecordService.findAllByPatientId(1)).thenReturn(records);

        // When
        List<MedicalRecordDTO> result = medicalRecordController.getMedicalRecordsByPatientId(1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(medicalRecordService).findAllByPatientId(1);
    }

    @Test
    void testCreateMedicalRecord_ShouldReturnMedicalRecordDTO() {
        // Given
        when(medicalRecordService.save(any(MedicalRecordDTO.class))).thenReturn(testMedicalRecordDTO);

        // When
        MedicalRecordDTO result = medicalRecordController.createMedicalRecord(testMedicalRecordDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecordId());
        verify(medicalRecordService).save(testMedicalRecordDTO);
    }

    @Test
    void testUpdateMedicalRecord_WhenExists_ShouldReturn200() {
        // Given
        testMedicalRecordDTO.setDiagnosis("Updated diagnosis");
        when(medicalRecordService.findById(1)).thenReturn(Optional.of(testMedicalRecordDTO));
        when(medicalRecordService.save(any(MedicalRecordDTO.class))).thenReturn(testMedicalRecordDTO);

        // When
        ResponseEntity<MedicalRecordDTO> response = medicalRecordController.updateMedicalRecord(1, testMedicalRecordDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getRecordId());
        verify(medicalRecordService).findById(1);
        verify(medicalRecordService).save(argThat(dto -> dto.getRecordId() == 1));
    }

    @Test
    void testUpdateMedicalRecord_WhenNotExists_ShouldReturn404() {
        // Given
        when(medicalRecordService.findById(999)).thenReturn(Optional.empty());

        // When
        ResponseEntity<MedicalRecordDTO> response = medicalRecordController.updateMedicalRecord(999, testMedicalRecordDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(medicalRecordService).findById(999);
        verify(medicalRecordService, never()).save(any());
    }

    @Test
    void testDeleteMedicalRecord_ShouldReturn204() {
        // Given
        doNothing().when(medicalRecordService).deleteById(1);

        // When
        ResponseEntity<Void> response = medicalRecordController.deleteMedicalRecord(1);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(medicalRecordService).deleteById(1);
    }
}

