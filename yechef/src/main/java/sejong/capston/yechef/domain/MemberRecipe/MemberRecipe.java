package sejong.capston.yechef.domain.MemberRecipe;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.global.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member_recipe SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted IS FALSE")
public class MemberRecipe extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  @NonNull
  private Boolean isLiked = false;

  public void toggleLike() {
    this.isLiked = !this.isLiked;
  }

}
