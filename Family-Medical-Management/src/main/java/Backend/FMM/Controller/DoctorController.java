package Backend.FMM.Controller;

import Backend.FMM.DTO.DoctorDTO;
import Backend.FMM.Service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public List<DoctorDTO> getAllDoctors() {
        return doctorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Integer id) {
        Optional<DoctorDTO> doctor = doctorService.findById(id);
        return doctor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DoctorDTO createDoctor(@RequestBody DoctorDTO dto) {
        return doctorService.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Integer id, @RequestBody DoctorDTO dto) {
        Optional<DoctorDTO> existing = doctorService.findById(id);
        if (existing.isPresent()) {
            dto.setDoctorId(id);
            return ResponseEntity.ok(doctorService.save(dto));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Integer id) {
        doctorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
