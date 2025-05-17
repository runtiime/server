package sejong.capston.yechef.domain.Recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sejong.capston.yechef.domain.Recipe.Recipe;

@Getter
@Builder
@AllArgsConstructor
public class RecipeDto {
  private Long id;
  private String title;
  private String author;
  private Recipe.RecipeType recipeType;
  private int likeCount;

  public static RecipeDto from(Recipe recipe) {
    return RecipeDto.builder()
        .id(recipe.getId())
        .title(recipe.getTitle())
        .author(recipe.getAuthor())
        .recipeType(recipe.getRecipeType())
        .likeCount(recipe.getLikeCount())
        .build();
  }
}

