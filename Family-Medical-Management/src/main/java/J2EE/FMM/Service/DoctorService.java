package J2EE.FMM.Service;

import J2EE.FMM.DTO.DoctorDTO;
import J2EE.FMM.Entity.Doctor;
import J2EE.FMM.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public DoctorDTO save(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setFullName(dto.getFullName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setClinicName(dto.getClinicName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setIsActive(dto.isActive());
        Doctor savedDoctor = doctorRepository.save(doctor);
        return toDTO(savedDoctor);
    }

    public Optional<DoctorDTO> findById(Integer id) {
        return doctorRepository.findById(id).map(this::toDTO);
    }

    public List<DoctorDTO> findAll() {
        return doctorRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        doctorRepository.deleteById(id);
    }

    private DoctorDTO toDTO(Doctor doctor) {
        return new DoctorDTO(doctor.getDoctorId(), doctor.getFullName(), doctor.getSpecialty(), doctor.getClinicName(),
                doctor.getEmail(), doctor.getPhone(), doctor.getLicenseNumber(), doctor.isActive());
    }
}
