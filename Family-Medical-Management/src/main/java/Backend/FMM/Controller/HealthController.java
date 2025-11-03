package Backend.FMM.Controller;

import Backend.FMM.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/health")
public class HealthController {
	@Autowired
	private UserRepository userRepository;

	@GetMapping
	public ResponseEntity<Map<String, Object>> health() {
		Map<String, Object> body = new HashMap<>();
		long userCount = userRepository.count();
		body.put("status", "UP");
		body.put("userCount", userCount);
		return ResponseEntity.ok(body);
	}
}
