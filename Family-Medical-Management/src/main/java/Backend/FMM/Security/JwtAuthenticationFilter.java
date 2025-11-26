package Backend.FMM.Security;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = getJWTFromRequest(request);

        if (jwt != null && tokenProvider.validateToken(jwt)) {
            try {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Debug log
                System.out.println("✓ Authentication set cho user: " + username + 
                    ", Authorities: " + userDetails.getAuthorities() + 
                    ", Request: " + request.getRequestURI());
            } catch (Exception e) {
                System.err.println("✗ Lỗi xử lý JWT token: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Log để debug - chỉ log khi không có token hoặc token không hợp lệ
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null) {
                System.out.println("✗ Request đến " + request.getRequestURI() + " không có Authorization header");
            } else if (!authHeader.startsWith("Bearer ")) {
                System.out.println("✗ Request đến " + request.getRequestURI() + " có Authorization header nhưng không đúng format: " + authHeader.substring(0, Math.min(20, authHeader.length())));
            } else if (jwt != null && !tokenProvider.validateToken(jwt)) {
                System.out.println("✗ Request đến " + request.getRequestURI() + " có token nhưng không hợp lệ");
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
