package sejong.capston.yechef.domain.Member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.capston.yechef.domain.Member.swagger.KakaoLoginApi;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;
import sejong.capston.yechef.global.login.dto.LoginResultDto;
import sejong.capston.yechef.global.login.service.KakaoOAuthService;
import sejong.capston.yechef.domain.Member.service.LoginService;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "로그인/회원가입", description = "로그인 api")
public class LoginController {

    private final LoginService loginService;
    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/login")
    public ResponseEntity<Void> getKakaoAuthUrl() {
        String kakaoAuthUrl = kakaoOAuthService.getKakaoAuthUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", kakaoAuthUrl)
                .build();
    }

    @KakaoLoginApi
    @GetMapping(value = "auth/login/code/kakao")
    public ResponseEntity<LoginResultDto> kakaoLogin(@RequestParam String code) {
        if(code == null) {
            throw BaseException.from(ErrorCode.KAKA0_CODE_REQUEST_FAILED);
        }
        return ResponseEntity.ok(loginService.loginWithKakao(code)); // 서비스에서 JWT 발급 & 쿠키 설정
    }

}