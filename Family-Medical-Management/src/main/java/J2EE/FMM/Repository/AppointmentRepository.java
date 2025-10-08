package J2EE.FMM.Repository;

import J2EE.FMM.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByPatient_PatientId(Integer patientId);
    List<Appointment> findByDoctor_DoctorId(Integer doctorId);
    List<Appointment> findByAppointmentDateBetween(java.sql.Timestamp start, java.sql.Timestamp end);
    List<Appointment> findByStatus(Appointment.Status status);
}
