package Backend.FMM.Service;

import Backend.FMM.Entity.Patient;
import Backend.FMM.Entity.MedicalRecord;
import Backend.FMM.Entity.Appointment;
import Backend.FMM.Entity.Doctor;
import Backend.FMM.Repository.PatientRepository;
import Backend.FMM.Repository.MedicalRecordRepository;
import Backend.FMM.Repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseContextServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private DatabaseContextService databaseContextService;

    private Patient testPatient;
    private MedicalRecord testRecord;
    private Appointment testAppointment;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setPatientId(1);
        testPatient.setFullName("John Doe");
        testPatient.setDateOfBirth(new Date(System.currentTimeMillis()));
        testPatient.setGender(Patient.Gender.MALE);
        testPatient.setBloodType("A+");
        testPatient.setEmergencyContact("123456789");
        testPatient.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        testDoctor = new Doctor();
        testDoctor.setDoctorId(1);
        testDoctor.setFullName("Dr. Smith");

        testRecord = new MedicalRecord();
        testRecord.setRecordId(1);
        testRecord.setPatient(testPatient);
        testRecord.setDiagnosis("Common cold");
        testRecord.setTreatment("Rest");
        testRecord.setMedications("Paracetamol");
        testRecord.setAllergies("None");
        testRecord.setNotes("Recovering");
        testRecord.setRecordDate(new Date(System.currentTimeMillis()));
        testRecord.setDoctorName("Dr. Smith");

        testAppointment = new Appointment();
        testAppointment.setAppointmentId(1);
        testAppointment.setPatient(testPatient);
        testAppointment.setDoctor(testDoctor);
        testAppointment.setAppointmentDate(new Timestamp(System.currentTimeMillis()));
        testAppointment.setStatus(Appointment.Status.SCHEDULED);
        testAppointment.setNotes("Regular checkup");
    }

    @Test
    void testBuildDatabaseContext_WithPatients_ShouldReturnContext() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(testPatient));
        when(medicalRecordRepository.findByPatient_PatientId(1)).thenReturn(Collections.emptyList());
        when(appointmentRepository.findByPatient_PatientId(1)).thenReturn(Collections.emptyList());

        // When
        String result = databaseContextService.buildDatabaseContext("test", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("THÔNG TIN BỆNH NHÂN"));
        assertTrue(result.contains("John Doe"));
        verify(patientRepository).findByUser_UserId(1);
    }

    @Test
    void testBuildDatabaseContext_WithNoData_ShouldReturnNoDataMessage() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Collections.emptyList());

        // When
        String result = databaseContextService.buildDatabaseContext("test", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Không tìm thấy thông tin"));
    }

    @Test
    void testBuildDatabaseContext_WithMedicalRecords_ShouldIncludeRecords() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(testPatient));
        when(medicalRecordRepository.findByPatient_PatientId(1)).thenReturn(Arrays.asList(testRecord));
        when(appointmentRepository.findByPatient_PatientId(1)).thenReturn(Collections.emptyList());

        // When
        String result = databaseContextService.buildDatabaseContext("test", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("HỒ SƠ Y TẾ"));
        assertTrue(result.contains("Common cold"));
    }

    @Test
    void testBuildDatabaseContext_WithAppointments_ShouldIncludeAppointments() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(testPatient));
        when(medicalRecordRepository.findByPatient_PatientId(1)).thenReturn(Collections.emptyList());
        when(appointmentRepository.findByPatient_PatientId(1)).thenReturn(Arrays.asList(testAppointment));

        // When
        String result = databaseContextService.buildDatabaseContext("test", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("LỊCH HẸN"));
        assertTrue(result.contains("SCHEDULED"));
    }

    @Test
    void testSmartQueryDatabase_WithPatientKeyword_ShouldReturnPatientInfo() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(testPatient));

        // When
        String result = databaseContextService.smartQueryDatabase("thông tin bệnh nhân", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("THÔNG TIN BỆNH NHÂN"));
    }

    @Test
    void testSmartQueryDatabase_WithMedicalKeyword_ShouldReturnMedicalInfo() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(testPatient));
        when(medicalRecordRepository.findByPatient_PatientId(1)).thenReturn(Arrays.asList(testRecord));

        // When
        String result = databaseContextService.smartQueryDatabase("hồ sơ y tế", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("HỒ SƠ Y TẾ"));
    }

    @Test
    void testSmartQueryDatabase_WithAppointmentKeyword_ShouldReturnAppointmentInfo() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(testPatient));
        when(appointmentRepository.findByPatient_PatientId(1)).thenReturn(Arrays.asList(testAppointment));

        // When
        String result = databaseContextService.smartQueryDatabase("lịch hẹn", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("LỊCH HẸN"));
    }

    @Test
    void testSmartQueryDatabase_WithNoKeyword_ShouldReturnAllData() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(testPatient));
        when(medicalRecordRepository.findByPatient_PatientId(1)).thenReturn(Collections.emptyList());
        when(appointmentRepository.findByPatient_PatientId(1)).thenReturn(Collections.emptyList());

        // When
        String result = databaseContextService.smartQueryDatabase("hello", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("THÔNG TIN BỆNH NHÂN"));
    }

    @Test
    void testSmartQueryDatabase_WithNoData_ShouldReturnNoDataMessage() {
        // Given
        when(patientRepository.findByUser_UserId(1)).thenReturn(Collections.emptyList());

        // When
        String result = databaseContextService.smartQueryDatabase("test", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Không tìm thấy thông tin"));
    }

    @Test
    void testBuildDatabaseContext_WithNullFields_ShouldHandleNulls() {
        // Given
        testPatient.setDateOfBirth(null);
        testPatient.setGender(null);
        testPatient.setBloodType(null);
        testPatient.setEmergencyContact(null);
        testPatient.setCreatedAt(null);
        when(patientRepository.findByUser_UserId(1)).thenReturn(Arrays.asList(testPatient));
        when(medicalRecordRepository.findByPatient_PatientId(1)).thenReturn(Collections.emptyList());
        when(appointmentRepository.findByPatient_PatientId(1)).thenReturn(Collections.emptyList());

        // When
        String result = databaseContextService.buildDatabaseContext("test", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("N/A"));
    }
}

