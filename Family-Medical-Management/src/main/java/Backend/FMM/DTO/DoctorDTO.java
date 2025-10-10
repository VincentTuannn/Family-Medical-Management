package Backend.FMM.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Integer doctorId;
    private String fullName;
    private String specialty;
    private String clinicName;
    private String email;
    private String phone;
    private String licenseNumber;
    private boolean isActive;
}
