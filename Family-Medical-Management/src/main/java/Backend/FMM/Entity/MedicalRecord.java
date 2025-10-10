package Backend.FMM.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Entity
@Table(name = "medical_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String medications; // JSON: [{"name": "Aspirin", "dosage": "100mg"}]

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String allergies; // JSON tương tự

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Temporal(TemporalType.DATE)
    @Column(name = "record_date", nullable = false)
    private Date recordDate = new Date(System.currentTimeMillis());

    @Column(name = "doctor_name", length = 100)
    private String doctorName;
}
