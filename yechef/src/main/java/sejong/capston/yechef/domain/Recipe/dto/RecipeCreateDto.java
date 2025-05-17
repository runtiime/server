package sejong.capston.yechef.domain.Recipe.dto;

import lombok.Builder;
import lombok.Getter;
import sejong.capston.yechef.domain.Recipe.Recipe;

@Getter
public class RecipeCreateDto {
  private String title;
  private Recipe.RecipeType recipeType;
}