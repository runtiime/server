package sejong.capston.yechef.domain.Recipe.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecipeIngredientDto {

  private String name;
  private String originalAmount;
  private String scaledAmount;

  public static RecipeIngredientDto of(String name, String originalAmount, String scaledAmount) {
    return RecipeIngredientDto.builder()
        .name(name)
        .originalAmount(originalAmount)
        .scaledAmount(scaledAmount)
        .build();
  }
}