package sejong.capston.yechef.global.login.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;
import sejong.capston.yechef.domain.Member.dto.LoginMemberDto;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    private final Key KEY;
    private final long accessTokenExpireMs;
    private final long refreshTokenExpireMs;

    public JwtProvider(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_EXPIRATION}") long accessTokenExpireMs,
            @Value("${JWT_REFRESH_EXPIRATION}") long refreshTokenExpireMs
    ) {
        this.KEY = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpireMs = accessTokenExpireMs;
        this.refreshTokenExpireMs = refreshTokenExpireMs;
    }

    // Access Token 생성
    public String createAccessToken(LoginMemberDto dto) {
        Claims claims = Jwts.claims();
        claims.put("memberId", dto.getId());
        claims.put("nickname", dto.getUsername());
        claims.put("role", dto.getRole());
        claims.put("type", "access");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpireMs))
                .signWith(KEY)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(LoginMemberDto dto) {
        Claims claims = Jwts.claims();
        claims.put("memberId", dto.getId());
        claims.put("nickname", dto.getUsername());
        claims.put("role", dto.getRole());
        claims.put("type", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpireMs))
                .signWith(KEY)
                .compact();
    }

    // Token 검증: Access, Refresh 공용
    public Claims parseClaims(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return jws.getBody();
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT: {}", e.getMessage());
            throw BaseException.from(ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT: {}", e.getMessage());
            throw BaseException.from(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    public boolean validateAccessToken(String token) {
        Claims claims = parseClaims(token);
        if (!"access".equals(claims.get("type", String.class))) {
            throw BaseException.from(ErrorCode.INVALID_JWT_TOKEN);
        }
        return true;
    }

    public boolean validateRefreshToken(String token) {
        Claims claims = parseClaims(token);
        if (!"refresh".equals(claims.get("type", String.class))) {
            throw BaseException.from(ErrorCode.INVALID_JWT_TOKEN);
        }
        return true;
    }

    // JWT → LoginMemberDto
    public LoginMemberDto getMemberDtoFromToken(String token) {
        Claims c = parseClaims(token);
        return LoginMemberDto.fromJwt(
                c.get("memberId", Long.class),
                c.get("nickname", String.class),
                Member.Role.valueOf(c.get("role", String.class))
        );
    }
}
