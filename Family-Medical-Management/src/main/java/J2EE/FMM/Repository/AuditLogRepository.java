package J2EE.FMM.Repository;

import J2EE.FMM.Entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByUser_UserId(Integer userId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByCreatedAtAfter(java.sql.Timestamp date);
}
