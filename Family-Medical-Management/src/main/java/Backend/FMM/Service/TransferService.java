package Backend.FMM.Service;

import Backend.FMM.DTO.TransferDTO;
import Backend.FMM.Entity.Transfer;
import Backend.FMM.Repository.UserRepository;
import Backend.FMM.Repository.PatientRepository;
import Backend.FMM.Repository.DoctorRepository;
import Backend.FMM.Repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class TransferService {
    @Autowired
    private TransferRepository transferRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	public TransferDTO save(TransferDTO dto) {
        Transfer transfer = new Transfer();
		// Set user, patient, doctor từ DTO nếu có
		if (dto.getUserId() != null) {
			userRepository.findById(dto.getUserId()).ifPresent(transfer::setUser);
		}
		if (dto.getPatientId() != null) {
			patientRepository.findById(dto.getPatientId()).ifPresent(transfer::setPatient);
		}
		if (dto.getDoctorId() != null) {
			doctorRepository.findById(dto.getDoctorId()).ifPresent(transfer::setDoctor);
		}
        transfer.setRecordIds(dto.getRecordIds().toString());  // Chuyển List sang JSON string
        transfer.setAccessType(Transfer.AccessType.valueOf(dto.getAccessType()));
        transfer.setExpiresAt(new Timestamp(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L));  // Hết hạn sau 7 ngày
        transfer.setStatus(Transfer.Status.PENDING);
        Transfer savedTransfer = transferRepository.save(transfer);
        return toDTO(savedTransfer);
    }

    public Optional<TransferDTO> findById(Integer id) {
        return transferRepository.findById(id).map(this::toDTO);
    }

    public List<TransferDTO> findAllByUserId(Integer userId) {
        return transferRepository.findByUser_UserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TransferDTO> findAll() {
        return transferRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        transferRepository.deleteById(id);
    }

    // Business logic: Check hết hạn
	public boolean isExpired(Integer transferId) {
		Optional<Transfer> transfer = transferRepository.findById(transferId);
		return transfer.map(t -> t.getExpiresAt() != null && t.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))).orElse(true);
	}

    private TransferDTO toDTO(Transfer transfer) {
        // Parse recordIds from JSON string to List<Integer>
        List<Integer> recordIds = null;
        if (transfer.getRecordIds() != null && !transfer.getRecordIds().isEmpty()) {
            try {
                // Simple JSON array parsing for [1,2,3] format
                String json = transfer.getRecordIds().replaceAll("[\\[\\]\\s]", "");
                if (!json.isEmpty()) {
                    recordIds = java.util.Arrays.stream(json.split(","))
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .collect(java.util.stream.Collectors.toList());
                }
            } catch (Exception e) {
                // If parsing fails, set to null
                recordIds = null;
            }
        }
        
        // Convert Timestamp to Date for DTO
        java.sql.Date expiresAtDate = transfer.getExpiresAt() != null ? 
            new java.sql.Date(transfer.getExpiresAt().getTime()) : null;
        java.sql.Date transferredAtDate = transfer.getTransferredAt() != null ? 
            new java.sql.Date(transfer.getTransferredAt().getTime()) : null;
            
		Integer userId = transfer.getUser() != null ? transfer.getUser().getUserId() : null;
		Integer patientId = transfer.getPatient() != null ? transfer.getPatient().getPatientId() : null;
		Integer doctorId = transfer.getDoctor() != null ? transfer.getDoctor().getDoctorId() : null;
		String accessType = transfer.getAccessType() != null ? transfer.getAccessType().name() : null;
		String status = transfer.getStatus() != null ? transfer.getStatus().name() : null;
		return new TransferDTO(transfer.getTransferId(), userId, patientId,
				doctorId, recordIds, accessType, expiresAtDate,
				status, transferredAtDate);
    }
}
