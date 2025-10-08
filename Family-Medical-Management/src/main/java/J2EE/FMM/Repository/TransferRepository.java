package J2EE.FMM.Repository;

import J2EE.FMM.Entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {
    List<Transfer> findByUser_UserId(Integer userId);
    List<Transfer> findByDoctor_DoctorId(Integer doctorId);
    List<Transfer> findByPatient_PatientIdAndStatus(Integer patientId, Transfer.Status status);
    List<Transfer> findByExpiresAtBefore(java.sql.Timestamp expiresAt);
}
