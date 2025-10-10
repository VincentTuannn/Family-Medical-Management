package Backend.FMM.Controller;

import Backend.FMM.DTO.PatientDTO;
import Backend.FMM.Service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Integer id) {
        Optional<PatientDTO> patient = patientService.findById(id);
        return patient.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<PatientDTO> getPatientsByUserId(@PathVariable Integer userId) {
        return patientService.findAllByUserId(userId);
    }

    @PostMapping
    public PatientDTO createPatient(@RequestBody PatientDTO dto) {
        return patientService.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Integer id, @RequestBody PatientDTO dto) {
        Optional<PatientDTO> existing = patientService.findById(id);
        if (existing.isPresent()) {
            dto.setPatientId(id);
            return ResponseEntity.ok(patientService.save(dto));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Integer id) {
        patientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
