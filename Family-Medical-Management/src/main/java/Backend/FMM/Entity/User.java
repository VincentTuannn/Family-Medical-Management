package Backend.FMM.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Convert(converter = RoleAttributeConverter.class)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "is_active")
    private boolean isActive = true;

//    public void setIsActive(boolean isActive) {
//        this.isActive = isActive;
//    }

    @PrePersist  // Tự set createdAt khi save
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = new Timestamp(System.currentTimeMillis());
        }
    }

    public enum Role {
        USER, DOCTOR, ADMIN;

        @JsonCreator
        public static Role fromString(String value) {
            if (value == null) {
                return USER; // Default role
            }
            try {
                // Thử parse trực tiếp (chữ hoa)
                return Role.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu không tìm thấy, trả về USER làm mặc định
                return USER;
            }
        }
    }
}
