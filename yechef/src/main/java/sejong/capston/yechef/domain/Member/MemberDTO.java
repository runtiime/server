package sejong.capston.yechef.domain.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDTO {
  private Long id;
  private String nickname;
  private String email;


}
