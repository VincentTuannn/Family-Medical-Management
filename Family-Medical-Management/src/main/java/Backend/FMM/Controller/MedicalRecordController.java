package Backend.FMM.Controller;

import Backend.FMM.DTO.MedicalRecordDTO;
import Backend.FMM.Service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-record")
public class MedicalRecordController {
    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping
    public List<MedicalRecordDTO> getAllMedicalRecords() {
        return medicalRecordService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordById(@PathVariable Integer id) {
        Optional<MedicalRecordDTO> record = medicalRecordService.findById(id);
        return record.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordDTO> getMedicalRecordsByPatientId(@PathVariable Integer patientId) {
        return medicalRecordService.findAllByPatientId(patientId);
    }

    @PostMapping
    public MedicalRecordDTO createMedicalRecord(@RequestBody MedicalRecordDTO dto) {
        return medicalRecordService.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDTO> updateMedicalRecord(@PathVariable Integer id, @RequestBody MedicalRecordDTO dto) {
        Optional<MedicalRecordDTO> existing = medicalRecordService.findById(id);
        if (existing.isPresent()) {
            dto.setRecordId(id);
            return ResponseEntity.ok(medicalRecordService.save(dto));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Integer id) {
        medicalRecordService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
