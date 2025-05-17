package sejong.capston.yechef.domain.Member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDto {
  private Long id;
  private String nickname;
  private String email;


}
