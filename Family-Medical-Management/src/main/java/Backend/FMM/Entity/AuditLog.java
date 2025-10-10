package Backend.FMM.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Có thể là Doctor nếu cần

    @Column(nullable = false, length = 50)
    private String action; // e.g., "transfer_record", "update_medical"

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String details; // JSON chi tiết

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}
