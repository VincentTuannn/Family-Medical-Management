package J2EE.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private Integer logId;
    private Integer userId;
    private String action;
    private String details;
    private String ipAddress;
    private Timestamp createdAt;
}
