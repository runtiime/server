package sejong.capston.yechef.domain.Recipe.dto;

import lombok.Builder;
import lombok.Getter;
import sejong.capston.yechef.domain.Recipe.Recipe;

@Getter
@Builder
public class OcrRecipeResultDto {
  private DetailRecipeDto recipe;
  private String rawText;

  public static OcrRecipeResultDto from(Recipe recipe, String rawText) {
    return OcrRecipeResultDto.builder()
        .recipe(DetailRecipeDto.from(recipe))
        .rawText(rawText)
        .build();
  }
}
