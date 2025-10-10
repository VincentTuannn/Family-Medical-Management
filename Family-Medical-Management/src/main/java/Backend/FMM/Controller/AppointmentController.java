package Backend.FMM.Controller;

import Backend.FMM.DTO.AppointmentDTO;
import Backend.FMM.Service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.findAll();
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
