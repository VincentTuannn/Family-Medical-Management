package Backend.FMM.Controller;

import Backend.FMM.DTO.PatientDTO;
import Backend.FMM.Service.PatientService;
import Backend.FMM.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public List<PatientDTO> getAllPatients() {
        return patientService.findAll();
    }

    @GetMapping("/my")
    public List<PatientDTO> getMyPatients(HttpServletRequest request) {
        // Lấy userId từ JWT token trong request header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Integer userId = jwtTokenProvider.getUserIdFromJWT(token);
            if (userId != null) {
                return patientService.findAllByUserId(userId);
            }
        }
        return List.of(); // Trả về danh sách rỗng nếu không lấy được userId
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
