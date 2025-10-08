package J2EE.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Integer appointmentId;
    private Integer patientId;
    private Integer doctorId;
    private Integer transferId;
    private Date appointmentDate;
    private String status; // "SCHEDULED", "COMPLETED", "CANCELLED"
    private String notes;
}
