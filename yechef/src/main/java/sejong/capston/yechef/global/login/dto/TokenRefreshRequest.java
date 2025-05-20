package sejong.capston.yechef.global.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class TokenRefreshRequest {
    private String refreshToken;
}
