package sejong.capston.yechef.domain.Ingredient;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.global.entity.BaseEntity;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE ingredient SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Ingredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull private String originalName;
    private String alternativeName;

    @NotNull private String originalAmount;

    @NotNull private String oneServingAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Builder
    public Ingredient(
            String originalName,
            String alternativeName,
            String originalAmount,
            String oneServingAmount,
            Recipe recipe
    ) {
        this.originalName = originalName;
        this.alternativeName = alternativeName;
        this.originalAmount = originalAmount;
        this.oneServingAmount = oneServingAmount;
        this.recipe = recipe;
    }
}
