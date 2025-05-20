package sejong.capston.yechef.global.login.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.capston.yechef.domain.Member.dto.LoginMemberDto;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;
import sejong.capston.yechef.global.login.dto.TokenRefreshRequest;
import sejong.capston.yechef.global.login.dto.TokenRefreshResponse;
import sejong.capston.yechef.global.login.jwt.JwtProvider;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtProvider jwtProvider;

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(
            @RequestBody TokenRefreshRequest request
    ) {
        String oldRefresh = request.getRefreshToken();
        // Refresh Token 검증 (만료, 서명, type 검사)
        jwtProvider.validateRefreshToken(oldRefresh);

        // Token 에서 사용자 정보 추출
        LoginMemberDto dto = jwtProvider.getMemberDtoFromToken(oldRefresh);

        // 새 Access / Refresh Token 발급
        String newAccess  = jwtProvider.createAccessToken(dto);
        String newRefresh = jwtProvider.createRefreshToken(dto);

        return ResponseEntity.ok(new TokenRefreshResponse(newAccess, newRefresh));
    }
}
