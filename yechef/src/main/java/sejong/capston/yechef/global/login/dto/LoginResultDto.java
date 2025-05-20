package sejong.capston.yechef.global.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.capston.yechef.domain.Member.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultDto {
    private String jwtToken;
    private Long memberId;
    private String userName;
    //private Member.Role role;
}
