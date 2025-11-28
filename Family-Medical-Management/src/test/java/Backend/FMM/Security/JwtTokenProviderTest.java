package Backend.FMM.Security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private String validJwtSecret = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbi10ZXN0aW5nLXB1cnBvc2VzLW9ubHk="; // Base64 encoded
    private long jwtExpirationInMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", validJwtSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", jwtExpirationInMs);
    }

    @Test
    void testGenerateToken_ShouldReturnValidToken() {
        // Given
        Authentication authentication = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        Collection<GrantedAuthority> authorities = Arrays.asList(authority);

        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authority.getAuthority()).thenReturn("ROLE_USER");

        // When
        String token = jwtTokenProvider.generateToken(authentication, 1);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        verify(authentication).getName();
        verify(authentication).getAuthorities();
    }

    @Test
    void testGetUsernameFromJWT_WithValidToken_ShouldReturnUsername() {
        // Given
        Authentication authentication = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        Collection<GrantedAuthority> authorities = Arrays.asList(authority);

        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authority.getAuthority()).thenReturn("ROLE_USER");

        String token = jwtTokenProvider.generateToken(authentication, 1);

        // When
        String username = jwtTokenProvider.getUsernameFromJWT(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void testGetUserIdFromJWT_WithValidToken_ShouldReturnUserId() {
        // Given
        Authentication authentication = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        Collection<GrantedAuthority> authorities = Arrays.asList(authority);

        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authority.getAuthority()).thenReturn("ROLE_USER");

        String token = jwtTokenProvider.generateToken(authentication, 123);

        // When
        Integer userId = jwtTokenProvider.getUserIdFromJWT(token);

        // Then
        assertNotNull(userId);
        assertEquals(123, userId);
    }

    @Test
    void testGetUserIdFromJWT_WithInvalidToken_ShouldReturnNull() {
        // When
        Integer userId = jwtTokenProvider.getUserIdFromJWT("invalid-token");

        // Then
        assertNull(userId);
    }

    @Test
    void testValidateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        Authentication authentication = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        Collection<GrantedAuthority> authorities = Arrays.asList(authority);

        when(authentication.getName()).thenReturn("testuser");
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authority.getAuthority()).thenReturn("ROLE_USER");

        String token = jwtTokenProvider.generateToken(authentication, 1);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_WithInvalidToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtTokenProvider.validateToken("invalid-token");

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_WithNullToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_WithEmptyToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtTokenProvider.validateToken("");

        // Then
        assertFalse(isValid);
    }
}

