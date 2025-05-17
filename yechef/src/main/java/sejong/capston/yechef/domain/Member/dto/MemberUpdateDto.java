package sejong.capston.yechef.domain.Member.dto;

import lombok.Data;

@Data
public class MemberUpdateDto {
  private String nickname;
  private String email;
  private boolean showExamplePhoto;
}
