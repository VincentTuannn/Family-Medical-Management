package Backend.FMM.DTO;

import java.sql.Date;

public class AppointmentDTO {
    private Integer appointmentId;
    private Integer patientId;
    private Integer doctorId;
    private Integer transferId;
    private Date appointmentDate;
    private String status; // "SCHEDULED", "COMPLETED", "CANCELLED".
    private String notes;

    public AppointmentDTO(Integer appointmentId, Integer patientId, Integer doctorId, Integer transferId, Date appointmentDate, String status, String notes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.transferId = transferId;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.notes = notes;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
