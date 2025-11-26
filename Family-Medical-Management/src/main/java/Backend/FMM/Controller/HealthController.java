package Backend.FMM.Controller;

import Backend.FMM.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class HealthController {
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/health")
	public ResponseEntity<Map<String, Object>> health() {
		Map<String, Object> body = new HashMap<>();
		long userCount = userRepository.count();
		body.put("status", "UP");
		body.put("userCount", userCount);
		return ResponseEntity.ok(body);
	}
	
	@GetMapping("/test-auth")
	public ResponseEntity<Map<String, Object>> testAuth() {
		Map<String, Object> body = new HashMap<>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
			body.put("authenticated", true);
			body.put("username", auth.getName());
			body.put("authorities", auth.getAuthorities().stream()
					.map(a -> a.getAuthority())
					.collect(Collectors.toList()));
		} else {
			body.put("authenticated", false);
			body.put("message", "Chưa được authenticate");
		}
		
		return ResponseEntity.ok(body);
	}
}
