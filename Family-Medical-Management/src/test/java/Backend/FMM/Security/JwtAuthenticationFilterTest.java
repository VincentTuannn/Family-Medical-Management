package Backend.FMM.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String validToken = "valid-token";
    private String username = "testuser";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/patient");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT(validToken)).thenReturn(username);

        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenProvider).validateToken(validToken);
        verify(tokenProvider).getUsernameFromJWT(validToken);
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(eq(request), any());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
    }

    @Test
    void testDoFilterInternal_WithNoAuthHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/patient");
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenProvider, never()).validateToken(anyString());
        verify(filterChain).doFilter(eq(request), any());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void testDoFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/patient");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(tokenProvider.validateToken("invalid-token")).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(tokenProvider, atLeastOnce()).validateToken("invalid-token");
        verify(tokenProvider, never()).getUsernameFromJWT(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(eq(request), any());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void testDoFilterInternal_WithAuthEndpoint_ShouldNotLog() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(eq(request), any());
    }

    @Test
    void testDoFilterInternal_WithInactiveUser_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/patient");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT(validToken)).thenReturn(username);

        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities("ROLE_USER")
                .disabled(true) // User is inactive
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(eq(request), any());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void testDoFilterInternal_WithUserNotFound_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/patient");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(eq(request), any());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void testDoFilterInternal_WithNoAuthorities_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/patient");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(tokenProvider.validateToken(validToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromJWT(validToken)).thenReturn(username);

        UserDetails userDetails = new User(username, "password", java.util.Collections.emptyList());

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(eq(request), any());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }
}

