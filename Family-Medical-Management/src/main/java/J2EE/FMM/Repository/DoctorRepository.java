package J2EE.FMM.Repository;

import J2EE.FMM.Entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findBySpecialtyAndIsActive(String specialty, boolean isActive);
}
