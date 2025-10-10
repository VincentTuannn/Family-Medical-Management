package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {
    private Integer recordId; //ID hồ sơ
    private Integer patientId; //ID bệnh nhân
    private String diagnosis; //Chẩn đoán
    private String treatment; //Điều trị
    private String medications; // Danh sách thuốc
    private String allergies; // Dị ứng
    private String notes; //Ghi chú
    private Date recordDate; //Ngày ghi nhận
    private String doctorName; //Tên bác sĩ ghi nhận
}
