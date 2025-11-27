package Backend.FMM.Service;

import Backend.FMM.Entity.User;
import Backend.FMM.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Log để debug
        System.out.println("📋 Loading user: " + username + 
            ", Role: " + user.getRole().name() + 
            ", Active: " + user.isActive());

        // Tạo UserDetails với role và active status
        // .roles() sẽ tự động thêm prefix "ROLE_" vào authorities
        // Ví dụ: roles("USER") -> authorities = ["ROLE_USER"]
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name()) // Tạo authorities với prefix ROLE_
                .disabled(!user.isActive()) // Set disabled nếu user không active
                .build();
    }
}
