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
    KAKAO_CONFIG_MISSING("KAKAO-0000", "카카오 로그인 설정 오류 발생.", ErrorDisplayType.POPUP),
    KAKA0_CODE_REQUEST_FAILED("KAKAO-0001", "카카오 인증 코드 요청 실패.", ErrorDisplayType.POPUP),
    KAKAO_TOKEN_REQUEST_FAILED("KAKAO-0002", "카카오 토큰 요청 실패", ErrorDisplayType.POPUP),
    KAKAO_USER_INFO_REQUEST_FAILED("KAKAO-0003", "카카오 사용자 정보 요청 실패", ErrorDisplayType.POPUP),

    //jwt
    JWT_TOKEN_EXPIRED("JWT-0000","JWT 토큰 유효기간 만료.",ErrorDisplayType.POPUP),
    INVALID_JWT_TOKEN("JWT-0001", "잘못된 JWT 형식입니다.", ErrorDisplayType.POPUP),

    //auth
    UNAUTHORIZED_REQUEST("AUTH-0000", "인증이 필요한 요청입니다", ErrorDisplayType.POPUP),

    //recipe
    RECIPE_NOT_EXIST("RCP-0000", "레시피가 존재하지 않습니다.", ErrorDisplayType.POPUP),
    NOT_RECIPE_OWNER("RCP-0001", "해당 사용자의 레시피가 아닙니다.", ErrorDisplayType.POPUP),
    NO_RECIPE_INFO_OF_LIKE("RCP-0002", "레시피 좋아요 정보가 없습니다.", ErrorDisplayType.POPUP),
    NOT_EXIST_THIS_STEP("RCP-0003", "레시피 해당 단계가 존재하지 않습니다.", ErrorDisplayType.POPUP),
    RECIPE_SAVE_FAILED("RCP-0004", "레시피 저장 실패", ErrorDisplayType.POPUP),
    RECIPE_INVALID_SERVINGS("RCP-0005", "인분 수는 1 이상 10 이하로만 설정 가능합니다.", ErrorDisplayType.POPUP),

    // memberRecipe
    MEMBER_RECIPE_NOT_FOUND("MRP-0000", "멤버레시피가 존재하지 않습니다.", ErrorDisplayType.POPUP),

    //gpt
    GPT_RESPONSE_PARSING_FAILED("GPT-0000", "GPT 응답 파싱에 실패했습니다.", ErrorDisplayType.POPUP),

    //s3
    FILE_UPLOAD_FAIL("S3-001", "파일 업로드에 실패했습니다.", ErrorDisplayType.TOAST),
    FILE_DELETE_FAIL("S3-002", "파일 삭제에 실패했습니다.", ErrorDisplayType.TOAST),
    FILE_UPLOAD_FAILED("S3-003", "파일 저장 실패", ErrorDisplayType.POPUP),

    // kakao img api
    KAKAO_API_ERROR("K-001", "카카오 이미지 검색 중 오류 발생", ErrorDisplayType.POPUP),
    IMAGE_SAVE_FAILED("K-002", "카카오 이미지 저장 실패", ErrorDisplayType.POPUP)
    ;

    private final String code;
    private final String message;
    private final ErrorDisplayType displayType;
}
