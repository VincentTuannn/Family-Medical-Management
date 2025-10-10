package Backend.FMM.Service;

import Backend.FMM.DTO.AppointmentDTO;
import Backend.FMM.Entity.Appointment;
import Backend.FMM.Repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public AppointmentDTO save(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        // Convert Date to Timestamp for entity
        if (dto.getAppointmentDate() != null) {
            appointment.setAppointmentDate(new java.sql.Timestamp(dto.getAppointmentDate().getTime()));
        }
        appointment.setStatus(Appointment.Status.valueOf(dto.getStatus()));
        appointment.setNotes(dto.getNotes());
        // Set patient, doctor, transfer từ DTO hoặc logic
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return toDTO(savedAppointment);
    }

    public Optional<AppointmentDTO> findById(Integer id) {
        return appointmentRepository.findById(id).map(this::toDTO);
    }

    public List<AppointmentDTO> findAllByPatientId(Integer patientId) {
        return appointmentRepository.findByPatient_PatientId(patientId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        appointmentRepository.deleteById(id);
    }

    private AppointmentDTO toDTO(Appointment appointment) {
        // Convert Timestamp to Date for DTO
        java.sql.Date appointmentDate = appointment.getAppointmentDate() != null ? 
            new java.sql.Date(appointment.getAppointmentDate().getTime()) : null;
            
        return new AppointmentDTO(appointment.getAppointmentId(), appointment.getPatient().getPatientId(),
                appointment.getDoctor().getDoctorId(), appointment.getTransfer() != null ? appointment.getTransfer().getTransferId() : null,
                appointmentDate, appointment.getStatus().name(), appointment.getNotes());
    }
}
