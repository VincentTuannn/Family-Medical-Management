package Backend.FMM.Service;

import Backend.FMM.DTO.PatientDTO;
import Backend.FMM.DTO.UserDTO;
import Backend.FMM.Entity.Patient;
import Backend.FMM.Repository.PatientRepository;
import Backend.FMM.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

	@Autowired
	private UserRepository userRepository;

	public PatientDTO save(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setFullName(dto.getFullName());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(Patient.Gender.valueOf(dto.getGender()));
        patient.setBloodType(dto.getBloodType());
        patient.setEmergencyContact(dto.getEmergencyContact());
		// Set user từ DTO nếu có
		if (dto.getUserId() != null) {
			userRepository.findById(dto.getUserId()).ifPresent(patient::setUser);
		}
        Patient savedPatient = patientRepository.save(patient);
        return toDTO(savedPatient);
    }

    public Optional<PatientDTO> findById(Integer id) {
        return patientRepository.findById(id).map(this::toDTO);
    }

    public List<PatientDTO> findAllByUserId(Integer userId) {
        return patientRepository.findByUser_UserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PatientDTO> findAll() {
        return patientRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        patientRepository.deleteById(id);
    }

	private PatientDTO toDTO(Patient patient) {
		Integer userId = patient.getUser() != null ? patient.getUser().getUserId() : null;
		String gender = patient.getGender() != null ? patient.getGender().name() : null;
		return new PatientDTO(patient.getPatientId(), userId, patient.getFullName(),
				patient.getDateOfBirth(), gender, patient.getBloodType(), patient.getEmergencyContact(),
				patient.getCreatedAt());
    }
}
