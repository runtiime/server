package sejong.capston.yechef.domain.Recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sejong.capston.yechef.domain.Recipe.Recipe;

@Getter
@AllArgsConstructor
@Builder
public class RecipeResponseDto {
  private Long id;
  private String title;
  private String author;
  private Recipe.RecipeType recipeType;

  public static RecipeResponseDto from(Recipe recipe) {
    return new RecipeResponseDto(
        recipe.getId(),
        recipe.getTitle(),
        recipe.getAuthor(),
        recipe.getRecipeType()
    );
  }
}

