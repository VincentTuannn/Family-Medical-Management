package J2EE.FMM.Repository;

import J2EE.FMM.Entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    List<MedicalRecord> findByPatient_PatientId(Integer patientId);
    List<MedicalRecord> findByPatient_PatientIdAndRecordDateBetween(Integer patientId, java.sql.Date startDate, java.sql.Date endDate);
}
