package sejong.capston.yechef.domain.RecipeSteps;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.global.entity.BaseEntity;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE recipe_step SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class RecipeStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull private int stepNumber;

    @NotNull @Lob private String description;

    @Lob private String action;
    private List<String> ingredients;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Builder
    public RecipeStep (int stepNumber, String action, List<String> ingredients,
    String description, Recipe recipe) {
        this.stepNumber = stepNumber;
        this.action = action;
        this.ingredients = ingredients;
        this.description = description;
        this.recipe = recipe;
    }

    public static RecipeStep of(int stepNumber, String action, List<String> ingredients,
                                String description, Recipe recipe) {
        return RecipeStep.builder()
                .stepNumber(stepNumber)
                .action(action)
                .ingredients(ingredients)
                .description(description)
                .recipe(recipe)
                .build();
    }
}
