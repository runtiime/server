package sejong.capston.yechef.global.login.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
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
    // 개발 기간 동안만 1주일 허용; 1000ms * 60s * 60m * 24h * 7d
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;

    public JwtProvider(@Value("${JWT_SECRET}") String secret) {
        this.KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // JWT 생성
    public String createAccessToken(LoginMemberDto loginMemberDto) {
        Claims claims = Jwts.claims();
        claims.put("memberId", loginMemberDto.getId());
        claims.put("username", loginMemberDto.getUsername());
        claims.put("role", loginMemberDto.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            log.info("토큰 검증 완료 - userId : {}, role : {}, 만료시간 : {}", claims.get("userId"),claims.get("role"), claims.getExpiration());
            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT: {}", e.getMessage());
            throw BaseException.from(ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.error("JWT 파싱 실패: {}", e.getMessage());
            throw BaseException.from(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    // JWT에서 사용자 ID 추출
    public Long getmemberIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("memberId", Long.class);
    }

    //JWT에서 사용자 이름 추출
    public String getNicknameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("nickname", String.class);
    }

    //JWT에서 사용자 권한 추출
    public Member.Role getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        String role = claims.get("role", String.class);
        return Member.Role.valueOf(role);
    }

    //JWT에서 userDto 추출
    public LoginMemberDto getMemberDto(String token) {
        return LoginMemberDto.fromJwt(
                getmemberIdFromToken(token),
                getNicknameFromToken(token),
                getRoleFromToken(token)
        );
    }


    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
