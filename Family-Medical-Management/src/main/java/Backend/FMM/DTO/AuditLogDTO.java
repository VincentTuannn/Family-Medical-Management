package Backend.FMM.DTO;

import java.sql.Timestamp;

public class AuditLogDTO {
    private Integer logId;
    private Integer userId;
    private String action;
    private String details;
    private String ipAddress;
    private Timestamp createdAt;

    public AuditLogDTO(Integer logId, Integer userId, String action, String details, String ipAddress, Timestamp createdAt) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
