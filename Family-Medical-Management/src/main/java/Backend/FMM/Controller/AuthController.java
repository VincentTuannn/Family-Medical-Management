package Backend.FMM.Controller;
import Backend.FMM.DTO.UserDTO;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.UserRepository;
import Backend.FMM.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginDTO) {
        try {
            String token = authService.authenticate(loginDTO.getUsername(), loginDTO.getPassword());
            return ResponseEntity.ok().body(Map.of("token", token, "message", "Login thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO dto) {
        try {
            User newUser = new User();
            newUser.setUsername(dto.getUsername());
            newUser.setEmail(dto.getEmail());
            newUser.setPassword(passwordEncoder.encode(dto.getPassword()));  // Encode trước khi save
            newUser.setPhone(dto.getPhone());
            newUser.setAddress(dto.getAddress());
            newUser.setRole(User.Role.USER);
            newUser.setIsActive(true);
            User saved = userRepository.save(newUser);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
