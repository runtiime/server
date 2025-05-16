package sejong.capston.yechef.global.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.response.ErrorResponse;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler {

    /**
     * 클라이언트 에러
     * 직접 생성한 예외에 대한 처리
     */
    @ExceptionHandler(BaseException.class)
    public ErrorResponse onThrowException(BaseException baseException) {
        log.error("⚠ Exception 발생: {} {}", baseException.getErrorCode(), baseException.getMessage());
        return ErrorResponse.generateErrorResponse(baseException);
    }

}
