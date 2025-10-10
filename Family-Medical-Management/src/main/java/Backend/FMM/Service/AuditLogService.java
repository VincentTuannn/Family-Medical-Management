package Backend.FMM.Service;

import Backend.FMM.DTO.AuditLogDTO;
import Backend.FMM.Entity.AuditLog;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    public AuditLogDTO save(AuditLogDTO dto) {
        AuditLog log = new AuditLog();
        log.setAction(dto.getAction());
        log.setDetails(dto.getDetails());
        log.setIpAddress(dto.getIpAddress());
        // Set user từ logic (e.g., từ auth)
        AuditLog savedLog = auditLogRepository.save(log);
        return toDTO(savedLog);
    }

    public List<AuditLogDTO> findAllByUserId(Integer userId) {
        return auditLogRepository.findByUser_UserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AuditLogDTO> findAll() {
        return auditLogRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Business logic: Log action (gọi từ controller)
    public void logAction(Integer userId, String action, String details, String ip) {
        AuditLog log = new AuditLog();
        User user = new User();
        user.setUserId(userId);
        log.setUser(user);  // Set user từ ID
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(ip);
        auditLogRepository.save(log);
    }

    private AuditLogDTO toDTO(AuditLog log) {
        return new AuditLogDTO(log.getLogId(), log.getUser() != null ? log.getUser().getUserId() : null, log.getAction(),
                log.getDetails(), log.getIpAddress(), log.getCreatedAt());
    }
}
