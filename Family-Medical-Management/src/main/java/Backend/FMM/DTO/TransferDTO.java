package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {
    private Integer transferId;
    private Integer userId;
    private Integer patientId;
    private Integer doctorId;
    private List<Integer> recordIds; // List ID hồ sơ
    private String accessType; // "VIEW", "EDIT"
    private Date expiresAt;
    private String status; // "PENDING", "APPROVED", "REVOKED"
    private Date transferredAt;
}
