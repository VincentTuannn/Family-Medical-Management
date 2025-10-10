package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer userId; //ID người dùng
    private String username; //Tên người dùng
    private String email; //Email đăng ký
    private String password; //Mật khẩu
    private String phone; //Số điện thoại
    private String address; //Địa chỉ
    private Timestamp createdAt; //Thời gian lúc được tạo ra
    private String role; // Vai trò: "USER", "DOCTOR", "ADMIN"
    private boolean isActive; //Trạng thái
}
