package Backend.FMM.Service;

import Backend.FMM.DTO.TransferDTO;
import Backend.FMM.Entity.Transfer;
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

    public TransferDTO save(TransferDTO dto) {
        Transfer transfer = new Transfer();
        // Set user, patient, doctor từ logic (e.g., từ auth)
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

    public void deleteById(Integer id) {
        transferRepository.deleteById(id);
    }

    // Business logic: Check hết hạn
    public boolean isExpired(Integer transferId) {
        Optional<Transfer> transfer = transferRepository.findById(transferId);
        return transfer.map(t -> t.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))).orElse(true);
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
            
        return new TransferDTO(transfer.getTransferId(), transfer.getUser().getUserId(), transfer.getPatient().getPatientId(),
                transfer.getDoctor().getDoctorId(), recordIds, transfer.getAccessType().name(), expiresAtDate,
                transfer.getStatus().name(), transferredAtDate);
    }
}
