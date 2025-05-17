package sejong.capston.yechef.domain.Member.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import sejong.capston.yechef.global.login.dto.LoginResultDto;
import sejong.capston.yechef.global.exception.response.ErrorResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "카카오 로그인/회원가입",
        description = "인가 코드를 받아 로그인 혹은 회원가입을 수행하고, JWT를 발급합니다."
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "로그인/회원 가입을 성공적으로 마쳤습니다.",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = LoginResultDto.class)
                )
        ),
        @ApiResponse(
                responseCode = "4xx",
                description = "요청 처리 실패",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponse.class)
                )
        )
})
public @interface KakaoLoginApi {
}