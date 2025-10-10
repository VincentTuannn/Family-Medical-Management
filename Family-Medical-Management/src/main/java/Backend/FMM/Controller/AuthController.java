package Backend.FMM.Controller;

import Backend.FMM.DTO.AuditLogDTO;
import Backend.FMM.Service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/audit-log")
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<AuditLogDTO> getAllAuditLogs() {
        return auditLogService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDTO> getAuditLogById(@PathVariable Integer id) {
        Optional<AuditLogDTO> log = auditLogService.findById(id);
        return log.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<AuditLogDTO> getAuditLogsByUserId(@PathVariable Integer userId) {
        return auditLogService.findAllByUserId(userId);
    }

    @PostMapping
    public AuditLogDTO createAuditLog(@RequestBody AuditLogDTO dto) {
        return auditLogService.save(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditLog(@PathVariable Integer id) {
        auditLogService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
