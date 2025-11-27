package Backend.FMM.Controller;

import Backend.FMM.DTO.AppointmentDTO;
import Backend.FMM.Service.AppointmentService;
import Backend.FMM.Service.PatientService;
import Backend.FMM.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.findAll();
    }

    @GetMapping("/my")
    public List<AppointmentDTO> getMyAppointments(HttpServletRequest request) {
        // Lấy userId từ JWT token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Integer userId = jwtTokenProvider.getUserIdFromJWT(token);
            if (userId != null) {
                // Lấy tất cả patients của user, sau đó lấy appointments của các patients đó
                var myPatients = patientService.findAllByUserId(userId);
                return myPatients.stream()
                    .flatMap(patient -> appointmentService.findAllByPatientId(patient.getPatientId()).stream())
                    .collect(Collectors.toList());
            }
        }
        return List.of();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Integer id) {
        Optional<AppointmentDTO> appointment = appointmentService.findById(id);
        return appointment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentDTO> getAppointmentsByPatientId(@PathVariable Integer patientId) {
        return appointmentService.findAllByPatientId(patientId);
    }

    @PostMapping
    public AppointmentDTO createAppointment(@RequestBody AppointmentDTO dto) {
        return appointmentService.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Integer id, @RequestBody AppointmentDTO dto) {
        Optional<AppointmentDTO> existing = appointmentService.findById(id);
        if (existing.isPresent()) {
            dto.setAppointmentId(id);
            return ResponseEntity.ok(appointmentService.save(dto));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Integer id) {
        appointmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
