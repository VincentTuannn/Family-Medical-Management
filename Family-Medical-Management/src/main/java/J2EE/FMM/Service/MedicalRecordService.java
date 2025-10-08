package J2EE.FMM.Service;

import J2EE.FMM.DTO.MedicalRecordDTO;
import J2EE.FMM.Entity.MedicalRecord;
import J2EE.FMM.Repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class MedicalRecordService {
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordDTO save(MedicalRecordDTO dto) {
        MedicalRecord record = new MedicalRecord();
        record.setDiagnosis(dto.getDiagnosis());
        record.setTreatment(dto.getTreatment());
        record.setMedications(dto.getMedications());
        record.setAllergies(dto.getAllergies());
        record.setNotes(dto.getNotes());
        record.setRecordDate(dto.getRecordDate());
        record.setDoctorName(dto.getDoctorName());
        // Set patient từ DTO hoặc logic
        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        return toDTO(savedRecord);
    }

    public Optional<MedicalRecordDTO> findById(Integer id) {
        return medicalRecordRepository.findById(id).map(this::toDTO);
    }

    public List<MedicalRecordDTO> findAllByPatientId(Integer patientId) {
        return medicalRecordRepository.findByPatient_PatientId(patientId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDTO toDTO(MedicalRecord record) {
        return new MedicalRecordDTO(record.getRecordId(), record.getPatient().getPatientId(), record.getDiagnosis(),
                record.getTreatment(), record.getMedications(), record.getAllergies(), record.getNotes(),
                record.getRecordDate(), record.getDoctorName());
    }
}
