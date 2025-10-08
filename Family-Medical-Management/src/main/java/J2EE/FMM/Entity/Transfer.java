package J2EE.FMM.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "transfer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String recordIds; // JSON array: [1, 2, 3]

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private AccessType accessType = AccessType.VIEW;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at")
    private Timestamp expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transferred_at", updatable = false)
    private Timestamp transferredAt = new Timestamp(System.currentTimeMillis());

    public enum AccessType {
        VIEW, EDIT
    }

    public enum Status {
        PENDING, APPROVED, REVOKED
    }
}
