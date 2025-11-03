package Backend.FMM.Service;

import Backend.FMM.DTO.MedicalRecordDTO;
import Backend.FMM.Entity.MedicalRecord;
import Backend.FMM.Repository.MedicalRecordRepository;
import Backend.FMM.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

	@Autowired
	private PatientRepository patientRepository;

	public MedicalRecordDTO save(MedicalRecordDTO dto) {
        MedicalRecord record = new MedicalRecord();
        record.setDiagnosis(dto.getDiagnosis());
        record.setTreatment(dto.getTreatment());
        record.setMedications(dto.getMedications());
        record.setAllergies(dto.getAllergies());
        record.setNotes(dto.getNotes());
        record.setRecordDate(dto.getRecordDate());
        record.setDoctorName(dto.getDoctorName());
		// Set patient từ DTO nếu có
		if (dto.getPatientId() != null) {
			patientRepository.findById(dto.getPatientId()).ifPresent(record::setPatient);
		}
        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        return toDTO(savedRecord);
    }

    public Optional<MedicalRecordDTO> findById(Integer id) {
        return medicalRecordRepository.findById(id).map(this::toDTO);
    }

    public List<MedicalRecordDTO> findAllByPatientId(Integer patientId) {
        return medicalRecordRepository.findByPatient_PatientId(patientId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MedicalRecordDTO> findAll() {
        return medicalRecordRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        medicalRecordRepository.deleteById(id);
    }

	private MedicalRecordDTO toDTO(MedicalRecord record) {
		Integer patientId = record.getPatient() != null ? record.getPatient().getPatientId() : null;
		return new MedicalRecordDTO(record.getRecordId(), patientId, record.getDiagnosis(),
				record.getTreatment(), record.getMedications(), record.getAllergies(), record.getNotes(),
				record.getRecordDate(), record.getDoctorName());
    }
}
