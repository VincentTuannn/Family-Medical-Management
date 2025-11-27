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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

        // Log tất cả requests để debug (trừ /api/auth/**)
        if (!request.getRequestURI().startsWith("/api/auth/")) {
            System.out.println("🔍 Filter processing: " + request.getMethod() + " " + request.getRequestURI());
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                System.out.println("   Authorization header: " + (authHeader.length() > 20 ? authHeader.substring(0, 20) + "..." : authHeader));
            } else {
                System.out.println("   ⚠️ Không có Authorization header");
            }
        }

        String jwt = getJWTFromRequest(request);

        if (jwt != null && tokenProvider.validateToken(jwt)) {
            try {
                String username = tokenProvider.getUsernameFromJWT(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Kiểm tra user có active không
                if (!userDetails.isEnabled()) {
                    System.err.println("✗ User " + username + " không active (bị khóa)");
                    System.err.println("  Request: " + request.getRequestURI() + ", Method: " + request.getMethod());
                    // Clear SecurityContext để đảm bảo không dùng authentication cũ
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                // Kiểm tra authorities
                if (userDetails.getAuthorities() == null || userDetails.getAuthorities().isEmpty()) {
                    System.err.println("✗ User " + username + " không có authorities");
                    System.err.println("  Request: " + request.getRequestURI() + ", Method: " + request.getMethod());
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Verify SecurityContext đã được set
                var context = SecurityContextHolder.getContext();
                var auth = context.getAuthentication();
                
                // Debug log chi tiết
                System.out.println("✓ Authentication set cho user: " + username + 
                    ", Authorities: " + userDetails.getAuthorities() + 
                    ", Request: " + request.getRequestURI() +
                    ", Method: " + request.getMethod());
                
                // Verify log
                if (auth != null && auth.isAuthenticated()) {
                    System.out.println("  ✓ SecurityContext verified - Authenticated: " + auth.isAuthenticated() + 
                        ", Authorities in context: " + auth.getAuthorities());
                } else {
                    System.err.println("  ✗ WARNING: SecurityContext không được set đúng!");
                }
            } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
                System.err.println("✗ User không tìm thấy: " + e.getMessage());
                System.err.println("  Request: " + request.getRequestURI() + ", Method: " + request.getMethod());
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                System.err.println("✗ Lỗi xử lý JWT token: " + e.getMessage());
                System.err.println("  Request: " + request.getRequestURI() + ", Method: " + request.getMethod());
                System.err.println("  Exception type: " + e.getClass().getName());
                e.printStackTrace();
                SecurityContextHolder.clearContext();
            }
        } else {
            // Log để debug - chỉ log khi không có token hoặc token không hợp lệ
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null) {
                // Chỉ log cho các request cần auth (trừ /api/auth/**)
                if (!request.getRequestURI().startsWith("/api/auth/")) {
                    System.out.println("✗ Request đến " + request.getRequestURI() + " không có Authorization header, Method: " + request.getMethod());
                }
            } else if (!authHeader.startsWith("Bearer ")) {
                System.out.println("✗ Request đến " + request.getRequestURI() + " có Authorization header nhưng không đúng format: " + authHeader.substring(0, Math.min(20, authHeader.length())));
            } else if (jwt != null && !tokenProvider.validateToken(jwt)) {
                System.out.println("✗ Request đến " + request.getRequestURI() + " có token nhưng không hợp lệ, Method: " + request.getMethod());
            }
        }

        // Wrap response để log status code
        var wrappedResponse = new jakarta.servlet.http.HttpServletResponseWrapper(response) {
            @Override
            public void setStatus(int sc) {
                super.setStatus(sc);
                if (sc == 403 && !request.getRequestURI().startsWith("/api/auth/")) {
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    System.err.println("❌ 403 Forbidden cho: " + request.getMethod() + " " + request.getRequestURI());
                    if (auth != null) {
                        System.err.println("   Authentication: " + auth.getName() + ", Authorities: " + auth.getAuthorities());
                    } else {
                        System.err.println("   Authentication: NULL (không có authentication)");
                    }
                }
            }
        };
        
        filterChain.doFilter(request, wrappedResponse);
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
