package Backend.FMM.Service;

import Backend.FMM.DTO.DoctorDTO;
import Backend.FMM.Entity.Doctor;
import Backend.FMM.Repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor testDoctor;
    private DoctorDTO testDoctorDTO;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setDoctorId(1);
        testDoctor.setFullName("Dr. John Doe");
        testDoctor.setSpecialty("Cardiology");
        testDoctor.setClinicName("Heart Clinic");
        testDoctor.setEmail("john@example.com");
        testDoctor.setPhone("123456789");
        testDoctor.setLicenseNumber("LIC123");
        testDoctor.setIsActive(true);

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
    void testSave_WhenCreatingNew_ShouldReturnSavedDoctor() {
        // Given
        testDoctorDTO.setDoctorId(null);
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor doctor = invocation.getArgument(0);
            doctor.setDoctorId(1);
            return doctor;
        });

        // When
        DoctorDTO result = doctorService.save(testDoctorDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getDoctorId());
        assertEquals("Dr. John Doe", result.getFullName());
        verify(doctorRepository).save(any(Doctor.class));
        verify(doctorRepository, never()).findById(any());
    }

    @Test
    void testSave_WhenUpdatingExisting_ShouldReturnUpdatedDoctor() {
        // Given
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When
        DoctorDTO result = doctorService.save(testDoctorDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getDoctorId());
        verify(doctorRepository).findById(1);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void testSave_WhenUpdatingNonExistent_ShouldThrowException() {
        // Given
        when(doctorRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            testDoctorDTO.setDoctorId(999);
            doctorService.save(testDoctorDTO);
        });
        verify(doctorRepository).findById(999);
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testFindById_WhenDoctorExists_ShouldReturnDoctorDTO() {
        // Given
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));

        // When
        Optional<DoctorDTO> result = doctorService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDoctor.getDoctorId(), result.get().getDoctorId());
        assertEquals(testDoctor.getFullName(), result.get().getFullName());
        verify(doctorRepository).findById(1);
    }

    @Test
    void testFindById_WhenDoctorNotExists_ShouldReturnEmpty() {
        // Given
        when(doctorRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<DoctorDTO> result = doctorService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(doctorRepository).findById(999);
    }

    @Test
    void testFindAll_ShouldReturnListOfDoctorDTOs() {
        // Given
        Doctor doctor2 = new Doctor();
        doctor2.setDoctorId(2);
        doctor2.setFullName("Dr. Jane Smith");
        List<Doctor> doctors = Arrays.asList(testDoctor, doctor2);
        when(doctorRepository.findAll()).thenReturn(doctors);

        // When
        List<DoctorDTO> result = doctorService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testDoctor.getDoctorId(), result.get(0).getDoctorId());
        assertEquals(doctor2.getDoctorId(), result.get(1).getDoctorId());
        verify(doctorRepository).findAll();
    }

    @Test
    void testDeleteById_ShouldCallRepository() {
        // Given
        doNothing().when(doctorRepository).deleteById(1);

        // When
        doctorService.deleteById(1);

        // Then
        verify(doctorRepository).deleteById(1);
    }
}


