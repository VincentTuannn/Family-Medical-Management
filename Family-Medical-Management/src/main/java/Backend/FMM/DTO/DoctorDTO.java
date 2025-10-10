package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Integer doctorId; //ID bác sĩ
    private String fullName; //Họ tên
    private String specialty; //Chuyên khoa
    private String clinicName; //Tên phòng khám
    private String email; //Email
    private String phone; //Số điện thoại
    private String licenseNumber; //Số giấy phép hành nghề
    private Boolean isActive; //Trạng thái hoạt động
}
