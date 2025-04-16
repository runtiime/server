package sejong.capston.yechef.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import jakarta.persistence.Id;
import lombok.*;
import sejong.capston.yechef.global.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private Long id;

  @NonNull
  @Column(nullable = false)
  private String nickname;

  @NonNull
  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private boolean showExamplePhoto = true;

  private String oauthId;

}
