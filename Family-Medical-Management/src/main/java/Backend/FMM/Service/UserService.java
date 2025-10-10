package Backend.FMM.Service;

import Backend.FMM.DTO.UserDTO;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Inject từ SecurityConfig

    public UserDTO save(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));  // Encode mật khẩu
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole(User.Role.valueOf(dto.getRole()));
        user.setIsActive(dto.getIsActive());
        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    public Optional<UserDTO> findById(Integer id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDTO);
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(),
                user.getPhone(), user.getAddress(), user.getCreatedAt(), user.getRole().name(), user.getIsActive());
    }
}
