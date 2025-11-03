package Backend.FMM.Service;

import Backend.FMM.DTO.AppointmentDTO;
import Backend.FMM.DTO.TransferDTO;
import Backend.FMM.Entity.Appointment;
import Backend.FMM.Repository.AppointmentRepository;
import Backend.FMM.Repository.PatientRepository;
import Backend.FMM.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	public AppointmentDTO save(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        // Convert Date to Timestamp for entity
        if (dto.getAppointmentDate() != null) {
            appointment.setAppointmentDate(new java.sql.Timestamp(dto.getAppointmentDate().getTime()));
        }
        appointment.setStatus(Appointment.Status.valueOf(dto.getStatus()));
        appointment.setNotes(dto.getNotes());
		// Set patient, doctor từ DTO IDs nếu có
		if (dto.getPatientId() != null) {
			patientRepository.findById(dto.getPatientId()).ifPresent(appointment::setPatient);
		}
		if (dto.getDoctorId() != null) {
			doctorRepository.findById(dto.getDoctorId()).ifPresent(appointment::setDoctor);
		}
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return toDTO(savedAppointment);
    }

    public Optional<AppointmentDTO> findById(Integer id) {
        return appointmentRepository.findById(id).map(this::toDTO);
    }

    public List<AppointmentDTO> findAllByPatientId(Integer patientId) {
        return appointmentRepository.findByPatient_PatientId(patientId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AppointmentDTO> findAll() {
        return appointmentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        appointmentRepository.deleteById(id);
    }

	private AppointmentDTO toDTO(Appointment appointment) {
        // Convert Timestamp to Date for DTO
		java.sql.Date appointmentDate = appointment.getAppointmentDate() != null ? 
			new java.sql.Date(appointment.getAppointmentDate().getTime()) : null;
		Integer patientId = appointment.getPatient() != null ? appointment.getPatient().getPatientId() : null;
		Integer doctorId = appointment.getDoctor() != null ? appointment.getDoctor().getDoctorId() : null;
		Integer transferId = appointment.getTransfer() != null ? appointment.getTransfer().getTransferId() : null;
		String status = appointment.getStatus() != null ? appointment.getStatus().name() : null;
		String notes = appointment.getNotes();
		return new AppointmentDTO(appointment.getAppointmentId(), patientId, doctorId, transferId, appointmentDate, status, notes);
    }
}
