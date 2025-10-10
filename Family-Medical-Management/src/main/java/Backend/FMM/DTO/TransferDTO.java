package Backend.FMM.DTO;

import java.sql.Date;
import java.util.List;

public class TransferDTO {
    private Integer transferId;
    private Integer userId;
    private Integer patientId;
    private Integer doctorId;
    private List<Integer> recordIds; // List ID hồ sơ
    private String accessType; // "VIEW", "EDIT"
    private Date expiresAt;
    private String status; // "PENDING", "APPROVED", "REVOKED"
    private Date transferredAt;

    public TransferDTO(Integer transferId, Integer userId, Integer patientId, Integer doctorId, List<Integer> recordIds, String accessType, Date expiresAt, String status, Date transferredAt) {
        this.transferId = transferId;
        this.userId = userId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.recordIds = recordIds;
        this.accessType = accessType;
        this.expiresAt = expiresAt;
        this.status = status;
        this.transferredAt = transferredAt;
    }

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public List<Integer> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Integer> recordIds) {
        this.recordIds = recordIds;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTransferredAt() {
        return transferredAt;
    }

    public void setTransferredAt(Date transferredAt) {
        this.transferredAt = transferredAt;
    }
}
