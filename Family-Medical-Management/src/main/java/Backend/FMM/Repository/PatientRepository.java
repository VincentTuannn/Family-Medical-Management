package Backend.FMM.Repository;

import Backend.FMM.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    List<Patient> findByUser_UserId(Integer userId);
    List<Patient> findByUser_UserIdAndCreatedAtAfter(Integer userId, java.sql.Timestamp date);
}
