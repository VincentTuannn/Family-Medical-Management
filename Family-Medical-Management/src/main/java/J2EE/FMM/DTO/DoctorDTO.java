package J2EE.FMM.DTO;

public class DoctorDTO {
    private Integer doctorId;
    private String fullName;
    private String specialty;
    private String clinicName;
    private String email;
    private String phone;
    private String licenseNumber;
    private boolean isActive;

    public DoctorDTO(Integer doctorId, String fullName, String specialty, String clinicName, String email, String phone, String licenseNumber, boolean isActive) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.specialty = specialty;
        this.clinicName = clinicName;
        this.email = email;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.isActive = isActive;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
