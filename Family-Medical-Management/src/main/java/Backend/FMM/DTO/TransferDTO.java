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
    private Integer transferId; //ID chuyển
    private Integer userId; //ID người dùng
    private Integer patientId; //ID bệnh nhân
    private Integer doctorId; //ID bác sĩ
    private List<Integer> recordIds; // List ID hồ sơ
    private String accessType; //Quyền truy cập: "VIEW", "EDIT"
    private Date expiresAt; //Ngày hết hạn chia sẻ
    private String status; //Trạng thái: "PENDING", "APPROVED", "REVOKED"
    private Date transferredAt; //Ngày chuyển
}
