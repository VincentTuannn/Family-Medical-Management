package Backend.FMM.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Integer appointmentId; //ID lịch hẹn
    private Integer patientId; //ID bệnh nhân
    private Integer doctorId; //ID bác sĩ
    private Integer transferId; //ID chuyển
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date appointmentDate; //Ngày giờ hẹn
    
    private String status; //Trạng thái: "SCHEDULED", "COMPLETED", "CANCELLED"
    private String notes; //Ghi chú
}
