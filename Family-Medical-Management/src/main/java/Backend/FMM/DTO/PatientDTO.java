package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Integer patientId; //ID bệnh nhân
    private Integer userId; //ID người dùng
    private String fullName; //Họ tên
    private Date dateOfBirth; //Ngày sinh
    private String gender; //Giới tính: "MALE", "FEMALE"
    private String bloodType; //Nhóm máu
    private String emergencyContact; //Người liên hệ khẩn cấp
    private Timestamp createdAt; //Ngày tạo
}
