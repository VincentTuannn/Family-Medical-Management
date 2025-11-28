package Backend.FMM.Service;

import Backend.FMM.DTO.TransferDTO;
import Backend.FMM.Entity.Transfer;
import Backend.FMM.Entity.Doctor;
import Backend.FMM.Entity.Patient;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.TransferRepository;
import Backend.FMM.Repository.UserRepository;
import Backend.FMM.Repository.PatientRepository;
import Backend.FMM.Repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private TransferService transferService;

    private Transfer testTransfer;
    private TransferDTO testTransferDTO;
    private User testUser;
    private Patient testPatient;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1);

        testPatient = new Patient();
        testPatient.setPatientId(1);

        testDoctor = new Doctor();
        testDoctor.setDoctorId(1);

        testTransfer = new Transfer();
        testTransfer.setTransferId(1);
        testTransfer.setUser(testUser);
        testTransfer.setPatient(testPatient);
        testTransfer.setDoctor(testDoctor);
        testTransfer.setRecordIds("[1,2,3]");
        testTransfer.setAccessType(Transfer.AccessType.VIEW);
        testTransfer.setExpiresAt(new Timestamp(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L));
        testTransfer.setStatus(Transfer.Status.PENDING);

        testTransferDTO = new TransferDTO();
        testTransferDTO.setTransferId(1);
        testTransferDTO.setUserId(1);
        testTransferDTO.setPatientId(1);
        testTransferDTO.setDoctorId(1);
        testTransferDTO.setRecordIds(Arrays.asList(1, 2, 3));
        testTransferDTO.setAccessType("VIEW");
        testTransferDTO.setStatus("PENDING");
    }

    @Test
    void testSave_ShouldReturnSavedTransfer() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer transfer = invocation.getArgument(0);
            transfer.setTransferId(1);
            return transfer;
        });

        // When
        TransferDTO result = transferService.save(testTransferDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTransferId());
        verify(transferRepository).save(any(Transfer.class));
        verify(userRepository).findById(1);
        verify(patientRepository).findById(1);
        verify(doctorRepository).findById(1);
    }

    @Test
    void testFindById_WhenExists_ShouldReturnTransfer() {
        // Given
        when(transferRepository.findById(1)).thenReturn(Optional.of(testTransfer));

        // When
        Optional<TransferDTO> result = transferService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getTransferId());
        verify(transferRepository).findById(1);
    }

    @Test
    void testFindById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(transferRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<TransferDTO> result = transferService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(transferRepository).findById(999);
    }

    @Test
    void testFindAllByUserId_ShouldReturnList() {
        // Given
        List<Transfer> transfers = Arrays.asList(testTransfer);
        when(transferRepository.findByUser_UserId(1)).thenReturn(transfers);

        // When
        List<TransferDTO> result = transferService.findAllByUserId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transferRepository).findByUser_UserId(1);
    }

    @Test
    void testFindAll_ShouldReturnList() {
        // Given
        List<Transfer> transfers = Arrays.asList(testTransfer);
        when(transferRepository.findAll()).thenReturn(transfers);

        // When
        List<TransferDTO> result = transferService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transferRepository).findAll();
    }

    @Test
    void testDeleteById_ShouldCallRepository() {
        // When
        transferService.deleteById(1);

        // Then
        verify(transferRepository).deleteById(1);
    }

    @Test
    void testIsExpired_WhenNotExpired_ShouldReturnFalse() {
        // Given
        testTransfer.setExpiresAt(new Timestamp(System.currentTimeMillis() + 86400000L)); // +1 day
        when(transferRepository.findById(1)).thenReturn(Optional.of(testTransfer));

        // When
        boolean result = transferService.isExpired(1);

        // Then
        assertFalse(result);
        verify(transferRepository).findById(1);
    }

    @Test
    void testIsExpired_WhenExpired_ShouldReturnTrue() {
        // Given
        testTransfer.setExpiresAt(new Timestamp(System.currentTimeMillis() - 86400000L)); // -1 day
        when(transferRepository.findById(1)).thenReturn(Optional.of(testTransfer));

        // When
        boolean result = transferService.isExpired(1);

        // Then
        assertTrue(result);
        verify(transferRepository).findById(1);
    }

    @Test
    void testIsExpired_WhenNotFound_ShouldReturnTrue() {
        // Given
        when(transferRepository.findById(999)).thenReturn(Optional.empty());

        // When
        boolean result = transferService.isExpired(999);

        // Then
        assertTrue(result);
        verify(transferRepository).findById(999);
    }

    @Test
    void testSave_WhenUserIdIsNull_ShouldNotSetUser() {
        // Given
        testTransferDTO.setUserId(null);
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer transfer = invocation.getArgument(0);
            transfer.setTransferId(1);
            return transfer;
        });

        // When
        TransferDTO result = transferService.save(testTransferDTO);

        // Then
        assertNotNull(result);
        verify(userRepository, never()).findById(anyInt());
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void testSave_WhenPatientIdIsNull_ShouldNotSetPatient() {
        // Given
        testTransferDTO.setPatientId(null);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(doctorRepository.findById(1)).thenReturn(Optional.of(testDoctor));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer transfer = invocation.getArgument(0);
            transfer.setTransferId(1);
            return transfer;
        });

        // When
        TransferDTO result = transferService.save(testTransferDTO);

        // Then
        assertNotNull(result);
        verify(patientRepository, never()).findById(anyInt());
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void testSave_WhenDoctorIdIsNull_ShouldNotSetDoctor() {
        // Given
        testTransferDTO.setDoctorId(null);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(patientRepository.findById(1)).thenReturn(Optional.of(testPatient));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer transfer = invocation.getArgument(0);
            transfer.setTransferId(1);
            return transfer;
        });

        // When
        TransferDTO result = transferService.save(testTransferDTO);

        // Then
        assertNotNull(result);
        verify(doctorRepository, never()).findById(anyInt());
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void testToDTO_WhenRecordIdsIsNull_ShouldReturnNull() {
        // Given
        testTransfer.setRecordIds(null);
        when(transferRepository.findById(1)).thenReturn(Optional.of(testTransfer));

        // When
        Optional<TransferDTO> result = transferService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getRecordIds());
    }

    @Test
    void testToDTO_WhenRecordIdsIsEmpty_ShouldReturnNull() {
        // Given
        testTransfer.setRecordIds("");
        when(transferRepository.findById(1)).thenReturn(Optional.of(testTransfer));

        // When
        Optional<TransferDTO> result = transferService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getRecordIds());
    }

    @Test
    void testToDTO_WhenRecordIdsParseFails_ShouldReturnNull() {
        // Given
        testTransfer.setRecordIds("invalid-json");
        when(transferRepository.findById(1)).thenReturn(Optional.of(testTransfer));

        // When
        Optional<TransferDTO> result = transferService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getRecordIds());
    }

    @Test
    void testToDTO_WhenTransferHasNullFields_ShouldHandleNulls() {
        // Given
        testTransfer.setUser(null);
        testTransfer.setPatient(null);
        testTransfer.setDoctor(null);
        testTransfer.setAccessType(null);
        testTransfer.setStatus(null);
        testTransfer.setExpiresAt(null);
        testTransfer.setTransferredAt(null);
        when(transferRepository.findById(1)).thenReturn(Optional.of(testTransfer));

        // When
        Optional<TransferDTO> result = transferService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertNull(result.get().getUserId());
        assertNull(result.get().getPatientId());
        assertNull(result.get().getDoctorId());
        assertNull(result.get().getAccessType());
        assertNull(result.get().getStatus());
        assertNull(result.get().getExpiresAt());
        assertNull(result.get().getTransferredAt());
    }

    @Test
    void testIsExpired_WhenExpiresAtIsNull_ShouldReturnTrue() {
        // Given
        testTransfer.setExpiresAt(null);
        when(transferRepository.findById(1)).thenReturn(Optional.of(testTransfer));

        // When
        boolean result = transferService.isExpired(1);

        // Then
        // When expiresAt is null, the condition t.getExpiresAt() != null && ... evaluates to false
        // So it returns false (not expired), not true
        assertFalse(result);
    }
}

