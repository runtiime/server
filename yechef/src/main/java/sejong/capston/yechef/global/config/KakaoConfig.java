package sejong.capston.yechef.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

@Component
@Getter
public class KakaoConfig {

    @Value("${spring.kakao.client-id}")
    private String clientId;

    @Value("${spring.kakao.redirect-uri}")
    private String redirect_uri;

    @Value("${spring.kakao.scope}")
    private String scope;

    @Value("${spring.kakao.authorization-uri}")
    private String authorization_uri;

    @Value("${spring.kakao.token-uri}")
    private String token_uri;

    @Value("${spring.kakao.user-info-uri}")
    private String user_info_uri;

    public void validate() {
        if (clientId == null || redirect_uri == null || scope == null || authorization_uri == null) {
            throw BaseException.from(ErrorCode.KAKAO_CONFIG_MISSING);
        }
    }
}