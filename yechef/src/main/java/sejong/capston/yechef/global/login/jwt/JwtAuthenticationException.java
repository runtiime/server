package sejong.capston.yechef.global.login.jwt;

import javax.security.sasl.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; // 스택 트레이스 생략
    }
}
