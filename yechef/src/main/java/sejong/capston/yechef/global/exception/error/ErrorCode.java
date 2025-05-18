package sejong.capston.yechef.global.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //client error : 4xx

    //member
    MEMBER_NOT_FOUND("MEM-0000", "해당 회원이 존재하지 않습니다.", ErrorDisplayType.POPUP),

    //kakao Error
    KAKAO_CONFIG_MISSING("KAKAO-0000", "카카오 로그인 설정 오류 발생.", ErrorDisplayType.HIDE),
    KAKA0_CODE_REQUEST_FAILED("KAKAO-0001", "카카오 인증 코드 요청 실패.", ErrorDisplayType.HIDE),
    KAKAO_TOKEN_REQUEST_FAILED("KAKAO-0002", "카카오 토큰 요청 실패", ErrorDisplayType.HIDE),
    KAKAO_USER_INFO_REQUEST_FAILED("KAKAO-0003", "카카오 사용자 정보 요청 실패", ErrorDisplayType.HIDE),

    //jwt
    JWT_TOKEN_EXPIRED("JWT-0000","JWT 토큰 유효기간 만료.",ErrorDisplayType.HIDE),
    INVALID_JWT_TOKEN("JWT-0001", "잘못된 JWT 형식입니다.", ErrorDisplayType.HIDE),

    //auth
    UNAUTHORIZED_REQUEST("auth-0000", "인증이 필요한 요청입니다", ErrorDisplayType.HIDE),

    //gpt
    GPT_RESPONSE_PARSING_FAILED("GPT-0000", "GPT 응답 파싱에 실패했습니다.", ErrorDisplayType.TOAST),

    //s3
    FILE_UPLOAD_FAIL("S3_001", "파일 업로드에 실패했습니다.", ErrorDisplayType.TOAST)

    ;

    private final String code;
    private final String message;
    private final ErrorDisplayType displayType;
}
