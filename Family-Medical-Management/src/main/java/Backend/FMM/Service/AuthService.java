package Backend.FMM.Service;
import Backend.FMM.Entity.User;
import Backend.FMM.Repository.UserRepository;
import Backend.FMM.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String authenticate(String username, String password) throws AuthenticationException {
        // Check user tồn tại trong DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User không tồn tại") {});

        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Sai mật khẩu");
        }

        // Check active
        if (!user.isActive()) {
            throw new DisabledException("Tài khoản bị khóa");
        }

        // Authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Generate JWT token
        return jwtUtil.generateToken(username);
    }
}
