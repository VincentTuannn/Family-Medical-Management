package Backend.FMM.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Integer patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @Convert(converter = GenderConverter.class)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    public enum Gender {
        MALE, FEMALE, OTHER;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static Gender fromString(String value) {
            if (value == null) {
                return MALE; // Default gender
            }
            try {
                // Chuyển chữ thường thành chữ hoa và parse
                return Gender.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu không tìm thấy, trả về MALE làm mặc định
                return MALE;
            }
        }
    }
}
