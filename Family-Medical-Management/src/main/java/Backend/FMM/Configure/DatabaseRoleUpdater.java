package Backend.FMM.Configure;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.role-updater.enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseRoleUpdater {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void updateRolesToUppercase() {
        // Chỉ chạy nếu JdbcTemplate đã sẵn sàng
        if (jdbcTemplate == null) {
            System.out.println("JdbcTemplate chưa sẵn sàng, bỏ qua việc cập nhật role");
            return;
        }

        try {
            // Kiểm tra xem bảng user có tồn tại không
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `user`", Integer.class);
            
            // Tắt safe update mode tạm thời
            jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 0");
            
            // Cập nhật tất cả role từ chữ thường sang chữ hoa
            int updatedRows = jdbcTemplate.update(
                "UPDATE `user` SET role = UPPER(role) WHERE role IN ('user', 'doctor', 'admin')"
            );
            
            // Bật lại safe update mode
            jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 1");
            
            if (updatedRows > 0) {
                System.out.println("✓ Đã cập nhật " + updatedRows + " bản ghi role từ chữ thường sang chữ hoa");
            } else {
                System.out.println("✓ Không có role nào cần cập nhật (tất cả đã là chữ hoa)");
            }
        } catch (Exception e) {
            // Nếu có lỗi, cố gắng bật lại safe update mode
            try {
                if (jdbcTemplate != null) {
                    jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 1");
                }
            } catch (Exception ignored) {}
            
            // Log warning nhưng không throw exception để không block startup
            System.out.println("⚠ Không thể cập nhật role (có thể database chưa có dữ liệu hoặc bảng chưa tồn tại): " + e.getMessage());
            System.out.println("⚠ Ứng dụng vẫn sẽ khởi động bình thường. Role sẽ được xử lý bởi RoleAttributeConverter.");
        }
    }
}

