package J2EE.FMM.DTO;

import java.sql.Date;

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

    public MedicalRecordDTO(Integer recordId, Integer patientId, String diagnosis, String treatment, String medications, String allergies, String notes, Date recordDate, String doctorName) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.medications = medications;
        this.allergies = allergies;
        this.notes = notes;
        this.recordDate = recordDate;
        this.doctorName = doctorName;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
