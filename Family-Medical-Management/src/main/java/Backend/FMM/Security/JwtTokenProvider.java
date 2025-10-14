package Backend.FMM.Security;

import io.jsonwebtoken.*;
import lombok.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    // Tạo JWT từ username
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Lấy username từ JWT
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Kiểm tra token hợp lệ hay không
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            System.out.println("Token không hợp lệ");
        } catch (ExpiredJwtException ex) {
            System.out.println("Token đã hết hạn");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Token không được hỗ trợ");
        } catch (IllegalArgumentException ex) {
            System.out.println("Chuỗi JWT trống");
        }
        return false;
    }
}
