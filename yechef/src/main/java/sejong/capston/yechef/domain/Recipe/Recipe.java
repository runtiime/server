package sejong.capston.yechef.domain.Recipe;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import sejong.capston.yechef.domain.Image.Image;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;
import sejong.capston.yechef.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE recipe SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull private String title;

    private int likeCount;
    @NonNull private String author;
    private double ranking;
    private int servings;

    @Enumerated(EnumType.STRING)
    private RecipeType recipeType;

    private boolean isUpdated;

    public enum RecipeType {
        PRIVATE,
        PUBLIC
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_recipe_id")
    private Recipe originalRecipeId;        // FK

    @OneToMany(mappedBy = "originalRecipeId", fetch = FetchType.LAZY)
    private List<Recipe> derivedRecipes;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStep> recipeSteps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MemberRecipe> memberRecipes = new ArrayList<>();

    @OneToOne(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Image image;

    @Builder
    public Recipe(
            Long id,
            String title,
            int likeCount,
            String author,
            double ranking,
            int servings,
            RecipeType recipeType,
            boolean isUpdated
    ) {
        this.id = id;
        this.title = title;
        this.likeCount = likeCount;
        this.author = author;
        this.ranking = ranking;
        this.servings = servings;
        this.recipeType = recipeType;
        this.isUpdated = isUpdated;
    }

    // 새 레시피 생성
    public static Recipe of(String title, String author, RecipeType recipeType, int servings) {
        return Recipe.builder()
                .title(title)
                .author(author)
                .likeCount(0)
                .ranking(0.0)
                .servings(servings)
                .recipeType(recipeType)
                .isUpdated(false)
                .build();
    }
}
