package J2EE.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {
    private Integer recordId;
    private Integer patientId;
    private String diagnosis;
    private String treatment;
    private String medications; // JSON string, e.g., [{"name":"Aspirin","dosage":"100mg"}]
    private String allergies; // JSON string
    private String notes;
    private Date recordDate;
    private String doctorName;
}
