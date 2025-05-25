package sejong.capston.yechef.domain.Recipe;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import sejong.capston.yechef.domain.Image.Image;
import sejong.capston.yechef.domain.Ingredient.Ingredient;
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
    @Lob private String text;

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
    private List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStep> recipeSteps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MemberRecipe> memberRecipes = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Image thumbnailImage;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Image sourceImage;

    public void addIngredient(Ingredient ing) {
        ingredients.add(ing);
        ing.setRecipe(this);
    }

    public void addStep(RecipeStep step) {
        recipeSteps.add(step);
        step.setRecipe(this);
    }

    @Builder
    public Recipe(
            Long id,
            String title,
            int likeCount,
            String author,
            double ranking,
            RecipeType recipeType,
            int servings,
            String text,
            boolean isUpdated,
            Image sourceImage
    ) {
        this.id = id;
        this.title = title;
        this.likeCount = likeCount;
        this.author = author;
        this.ranking = ranking;
        this.recipeType = recipeType;
        this.servings = servings;
        this.text = text;
        this.isUpdated = isUpdated;
        this.sourceImage = sourceImage;
    }

    // 새 레시피 생성
    public static Recipe of(String title, String author, RecipeType recipeType,
                            int servings, String text, Image sourceImage) {
        return Recipe.builder()
                .title(title)
                .author(author)
                .likeCount(0)
                .ranking(0.0)
                .recipeType(recipeType)
                .servings(servings)
                .text(text)
                .isUpdated(false)
                .sourceImage(sourceImage)
                .build();
    }
}
