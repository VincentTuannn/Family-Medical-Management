package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Integer logId; //ID log
    private Integer userId; //ID người dùng
    private String action; //Hành động
    private String details; //Chi tiết
    private String ipAddress; //IP người dùng
    private Timestamp createdAt; //Thời gian
}
