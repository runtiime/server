package sejong.capston.yechef.global.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorDisplayType;
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

    /**
     * 404 Not Found 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("[404] URI: {}, Method: {}", request.getRequestURI(), request.getMethod());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("404", "Not Found", ErrorDisplayType.TOAST));
    }

    /**
     * 500 Internal Server Error 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("[500] URI: {}, Method: {}, Message: {}", request.getRequestURI(), request.getMethod(), ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("500", "Internal Server Error", ErrorDisplayType.TOAST));
    }

}
