package Backend.FMM.Service;

import Backend.FMM.DTO.AppointmentDTO;
import Backend.FMM.Entity.Appointment;
import Backend.FMM.Entity.Doctor;
import Backend.FMM.Entity.Patient;
import Backend.FMM.Repository.AppointmentRepository;
import Backend.FMM.Repository.DoctorRepository;
import Backend.FMM.Repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private AppointmentDTO testAppointmentDTO;
    private Patient testPatient;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setPatientId(1);
        testPatient.setFullName("John Doe");

        testDoctor = new Doctor();
        testDoctor.setDoctorId(1);
        testDoctor.setFullName("Dr. Smith");

        testAppointment = new Appointment();
        testAppointment.setAppointmentId(1);
        testAppointment.setAppointmentDate(new Timestamp(System.currentTimeMillis()));
        testAppointment.setStatus(Appointment.Status.SCHEDULED);
        testAppointment.setNotes("Regular checkup");
        testAppointment.setPatient(testPatient);
        testAppointment.setDoctor(testDoctor);

        testAppointmentDTO = new AppointmentDTO();
        testAppointmentDTO.setAppointmentId(1);
        testAppointmentDTO.setPatientId(1);
        testAppointmentDTO.setDoctorId(1);
        testAppointmentDTO.setAppointmentDate(new Date());
        testAppointmentDTO.setStatus("SCHEDULED");
        testAppointmentDTO.setNotes("Regular checkup");
    }

    @Test
    void testSave_WhenCreatingNew_ShouldReturnSavedAppointment() {
        // Given
        testAppointmentDTO.setAppointmentId(null);
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setAppointmentId(1);
            return appointment;
        });

        // When
        AppointmentDTO result = appointmentService.save(testAppointmentDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAppointmentId());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(patientRepository).findById(1);
        verify(doctorRepository).findById(1);
    }

    @Test
    void testSave_WhenUpdatingExisting_ShouldReturnUpdatedAppointment() {
        // Given
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(testAppointment));
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        AppointmentDTO result = appointmentService.save(testAppointmentDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAppointmentId());
        verify(appointmentRepository).findById(1);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void testSave_WhenUpdatingNotFound_ShouldThrowException() {
        // Given
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            testAppointmentDTO.setAppointmentId(999);
            appointmentService.save(testAppointmentDTO);
        });
        verify(appointmentRepository).findById(999);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void testFindById_WhenExists_ShouldReturnAppointment() {
        // Given
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(testAppointment));

        // When
        Optional<AppointmentDTO> result = appointmentService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getAppointmentId());
        verify(appointmentRepository).findById(1);
    }

    @Test
    void testFindById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<AppointmentDTO> result = appointmentService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(appointmentRepository).findById(999);
    }

    @Test
    void testFindAllByPatientId_ShouldReturnList() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByPatient_PatientId(1)).thenReturn(appointments);

        // When
        List<AppointmentDTO> result = appointmentService.findAllByPatientId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findByPatient_PatientId(1);
    }

    @Test
    void testFindAll_ShouldReturnList() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        // When
        List<AppointmentDTO> result = appointmentService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findAll();
    }

    @Test
    void testDeleteById_ShouldCallRepository() {
        // When
        appointmentService.deleteById(1);

        // Then
        verify(appointmentRepository).deleteById(1);
    }

    @Test
    void testSave_WhenAppointmentDateIsNull_ShouldNotSetDate() {
        // Given
        testAppointmentDTO.setAppointmentId(null);
        testAppointmentDTO.setAppointmentDate(null);
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setAppointmentId(1);
            return appointment;
        });

        // When
        AppointmentDTO result = appointmentService.save(testAppointmentDTO);

        // Then
        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void testSave_WhenStatusIsNull_ShouldNotSetStatus() {
        // Given
        testAppointmentDTO.setAppointmentId(null);
        testAppointmentDTO.setStatus(null);
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setAppointmentId(1);
            return appointment;
        });

        // When
        AppointmentDTO result = appointmentService.save(testAppointmentDTO);

        // Then
        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void testSave_WhenPatientIdIsNull_ShouldNotSetPatient() {
        // Given
        testAppointmentDTO.setAppointmentId(null);
        testAppointmentDTO.setPatientId(null);
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setAppointmentId(1);
            return appointment;
        });

        // When
        AppointmentDTO result = appointmentService.save(testAppointmentDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository, never()).findById(anyInt());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void testSave_WhenDoctorIdIsNull_ShouldNotSetDoctor() {
        // Given
        testAppointmentDTO.setAppointmentId(null);
        testAppointmentDTO.setDoctorId(null);
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setAppointmentId(1);
            return appointment;
        });

        // When
        AppointmentDTO result = appointmentService.save(testAppointmentDTO);

        // Then
        assertNotNull(result);
        verify(doctorRepository, never()).findById(anyInt());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void testToDTO_WhenAppointmentHasNullFields_ShouldHandleNulls() {
        // Given
        testAppointment.setPatient(null);
        testAppointment.setDoctor(null);
        testAppointment.setTransfer(null);
        testAppointment.setStatus(null);
        testAppointment.setAppointmentDate(null);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(testAppointment));

        // When
        Optional<AppointmentDTO> result = appointmentService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getPatientId());
        assertNull(result.get().getDoctorId());
        assertNull(result.get().getTransferId());
        assertNull(result.get().getStatus());
        assertNull(result.get().getAppointmentDate());
    }
}


