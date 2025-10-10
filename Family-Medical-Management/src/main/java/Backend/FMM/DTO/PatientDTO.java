package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private Integer patientId;
    private Integer userId;
    private String fullName;
    private Date dateOfBirth;
    private String gender; // "MALE", "FEMALE"
    private String bloodType;
    private String emergencyContact;
    private Timestamp createdAt;
}
