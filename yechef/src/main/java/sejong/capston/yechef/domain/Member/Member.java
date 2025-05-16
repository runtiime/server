package sejong.capston.yechef.domain.Member;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import lombok.*;
import sejong.capston.yechef.global.entity.BaseEntity;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  @NonNull private String nickname;
  @NonNull private Long oauthId;
  @Column(unique = true) private String email;
  private boolean showExamplePhoto = true;

  @Enumerated(EnumType.STRING) private Role role;

  public enum Role {
    USER,
    ADMIN
  }

  @Builder
  public Member(String nickname, String email, Long oauthId, Role role) {
    this.nickname = nickname;
    this.email = email;
    this.oauthId = oauthId;
    this.role = role;
  }
}
