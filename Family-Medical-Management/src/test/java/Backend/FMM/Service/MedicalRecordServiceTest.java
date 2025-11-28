package Backend.FMM.Service;

import Backend.FMM.DTO.MedicalRecordDTO;
import Backend.FMM.Entity.MedicalRecord;
import Backend.FMM.Entity.Patient;
import Backend.FMM.Repository.MedicalRecordRepository;
import Backend.FMM.Repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private MedicalRecord testRecord;
    private MedicalRecordDTO testRecordDTO;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setPatientId(1);
        testPatient.setFullName("John Doe");

        testRecord = new MedicalRecord();
        testRecord.setRecordId(1);
        testRecord.setDiagnosis("Common cold");
        testRecord.setTreatment("Rest and fluids");
        testRecord.setMedications("Paracetamol");
        testRecord.setAllergies("None");
        testRecord.setNotes("Patient recovering well");
        testRecord.setRecordDate(new Date(System.currentTimeMillis()));
        testRecord.setDoctorName("Dr. Smith");
        testRecord.setPatient(testPatient);

        testRecordDTO = new MedicalRecordDTO();
        testRecordDTO.setRecordId(1);
        testRecordDTO.setPatientId(1);
        testRecordDTO.setDiagnosis("Common cold");
        testRecordDTO.setTreatment("Rest and fluids");
        testRecordDTO.setMedications("Paracetamol");
        testRecordDTO.setAllergies("None");
        testRecordDTO.setNotes("Patient recovering well");
        testRecordDTO.setRecordDate(new Date(System.currentTimeMillis()));
        testRecordDTO.setDoctorName("Dr. Smith");
    }

    @Test
    void testSave_WhenCreatingNew_ShouldReturnSavedRecord() {
        // Given
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(invocation -> {
            MedicalRecord record = invocation.getArgument(0);
            record.setRecordId(1);
            return record;
        });

        // When
        MedicalRecordDTO result = medicalRecordService.save(testRecordDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getRecordId());
        assertEquals("Common cold", result.getDiagnosis());
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(patientRepository).findById(1);
    }

    @Test
    void testSave_WhenPatientNotFound_ShouldNotSetPatient() {
        // Given
        when(patientRepository.findById(999)).thenReturn(Optional.empty());
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(invocation -> {
            MedicalRecord record = invocation.getArgument(0);
            record.setRecordId(1);
            return record;
        });

        // When
        testRecordDTO.setPatientId(999);
        MedicalRecordDTO result = medicalRecordService.save(testRecordDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository).findById(999);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void testFindById_WhenExists_ShouldReturnRecord() {
        // Given
        when(medicalRecordRepository.findById(1)).thenReturn(Optional.of(testRecord));

        // When
        Optional<MedicalRecordDTO> result = medicalRecordService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getRecordId());
        assertEquals("Common cold", result.get().getDiagnosis());
        verify(medicalRecordRepository).findById(1);
    }

    @Test
    void testFindById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(medicalRecordRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<MedicalRecordDTO> result = medicalRecordService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(medicalRecordRepository).findById(999);
    }

    @Test
    void testFindAllByPatientId_ShouldReturnList() {
        // Given
        List<MedicalRecord> records = Arrays.asList(testRecord);
        when(medicalRecordRepository.findByPatient_PatientId(1)).thenReturn(records);

        // When
        List<MedicalRecordDTO> result = medicalRecordService.findAllByPatientId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository).findByPatient_PatientId(1);
    }

    @Test
    void testFindAll_ShouldReturnList() {
        // Given
        List<MedicalRecord> records = Arrays.asList(testRecord);
        when(medicalRecordRepository.findAll()).thenReturn(records);

        // When
        List<MedicalRecordDTO> result = medicalRecordService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(medicalRecordRepository).findAll();
    }

    @Test
    void testDeleteById_ShouldCallRepository() {
        // When
        medicalRecordService.deleteById(1);

        // Then
        verify(medicalRecordRepository).deleteById(1);
    }

    @Test
    void testSave_WhenPatientIdIsNull_ShouldNotSetPatient() {
        // Given
        testRecordDTO.setPatientId(null);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(invocation -> {
            MedicalRecord record = invocation.getArgument(0);
            record.setRecordId(1);
            return record;
        });

        // When
        MedicalRecordDTO result = medicalRecordService.save(testRecordDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository, never()).findById(anyInt());
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void testToDTO_WhenRecordHasNullPatient_ShouldReturnNullPatientId() {
        // Given
        testRecord.setPatient(null);
        when(medicalRecordRepository.findById(1)).thenReturn(Optional.of(testRecord));

        // When
        Optional<MedicalRecordDTO> result = medicalRecordService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getPatientId());
    }
}

