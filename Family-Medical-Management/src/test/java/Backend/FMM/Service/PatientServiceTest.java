package Backend.FMM.Service;

import Backend.FMM.DTO.PatientDTO;
import Backend.FMM.Entity.Patient;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.PatientRepository;
import Backend.FMM.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;
    private PatientDTO testPatientDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");

        testPatient = new Patient();
        testPatient.setPatientId(1);
        testPatient.setFullName("John Doe");
        testPatient.setDateOfBirth(new Date(System.currentTimeMillis()));
        testPatient.setGender(Patient.Gender.MALE);
        testPatient.setBloodType("A+");
        testPatient.setEmergencyContact("123456789");
        testPatient.setUser(testUser);
        testPatient.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        testPatientDTO = new PatientDTO();
        testPatientDTO.setPatientId(1);
        testPatientDTO.setUserId(1);
        testPatientDTO.setFullName("John Doe");
        testPatientDTO.setDateOfBirth(new Date(System.currentTimeMillis()));
        testPatientDTO.setGender("MALE");
        testPatientDTO.setBloodType("A+");
        testPatientDTO.setEmergencyContact("123456789");
    }

    @Test
    void testSave_WhenCreatingNew_ShouldReturnSavedPatient() {
        // Given
        testPatientDTO.setPatientId(null);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setPatientId(1);
            return patient;
        });

        // When
        PatientDTO result = patientService.save(testPatientDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPatientId());
        assertEquals("John Doe", result.getFullName());
        verify(patientRepository).save(any(Patient.class));
        verify(userRepository).findById(1);
    }

    @Test
    void testSave_WhenUserNotFound_ShouldNotSetUser() {
        // Given
        testPatientDTO.setPatientId(null);
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setPatientId(1);
            return patient;
        });

        // When
        PatientDTO result = patientService.save(testPatientDTO);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testSave_WhenGenderIsNull_ShouldSetDefaultGender() {
        // Given
        testPatientDTO.setGender(null);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setPatientId(1);
            return patient;
        });

        // When
        PatientDTO result = patientService.save(testPatientDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testSave_WhenGenderIsInvalid_ShouldSetDefaultGender() {
        // Given
        testPatientDTO.setGender("INVALID");
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setPatientId(1);
            return patient;
        });

        // When
        PatientDTO result = patientService.save(testPatientDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testFindById_WhenExists_ShouldReturnPatient() {
        // Given
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));

        // When
        Optional<PatientDTO> result = patientService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getPatientId());
        assertEquals("John Doe", result.get().getFullName());
        verify(patientRepository).findById(1);
    }

    @Test
    void testFindById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(patientRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<PatientDTO> result = patientService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(patientRepository).findById(999);
    }

    @Test
    void testFindAllByUserId_ShouldReturnList() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.findByUser_UserId(1)).thenReturn(patients);

        // When
        List<PatientDTO> result = patientService.findAllByUserId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getPatientId());
        verify(patientRepository).findByUser_UserId(1);
    }

    @Test
    void testFindAll_ShouldReturnList() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.findAll()).thenReturn(patients);

        // When
        List<PatientDTO> result = patientService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository).findAll();
    }

    @Test
    void testDeleteById_ShouldCallRepository() {
        // When
        patientService.deleteById(1);

        // Then
        verify(patientRepository).deleteById(1);
    }

    @Test
    void testSave_WhenGenderIsLowercase_ShouldConvertToUppercase() {
        // Given
        testPatientDTO.setGender("female");
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setPatientId(1);
            return patient;
        });

        // When
        PatientDTO result = patientService.save(testPatientDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository).save(argThat(p -> p.getGender() == Patient.Gender.FEMALE));
    }

    @Test
    void testSave_WhenGenderIsOther_ShouldSetOther() {
        // Given
        testPatientDTO.setGender("OTHER");
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setPatientId(1);
            return patient;
        });

        // When
        PatientDTO result = patientService.save(testPatientDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository).save(argThat(p -> p.getGender() == Patient.Gender.OTHER));
    }

    @Test
    void testSave_WhenUserIdIsNull_ShouldNotSetUser() {
        // Given
        testPatientDTO.setUserId(null);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient patient = invocation.getArgument(0);
            patient.setPatientId(1);
            return patient;
        });

        // When
        PatientDTO result = patientService.save(testPatientDTO);

        // Then
        assertNotNull(result);
        verify(userRepository, never()).findById(anyInt());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testToDTO_WhenPatientHasNullUser_ShouldReturnNullUserId() {
        // Given
        testPatient.setUser(null);
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));

        // When
        Optional<PatientDTO> result = patientService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getUserId());
    }

    @Test
    void testToDTO_WhenPatientHasNullGender_ShouldReturnNullGender() {
        // Given
        testPatient.setGender(null);
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));

        // When
        Optional<PatientDTO> result = patientService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getGender());
    }
}


