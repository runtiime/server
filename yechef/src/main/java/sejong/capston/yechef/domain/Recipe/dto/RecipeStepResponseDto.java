package sejong.capston.yechef.domain.Recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RecipeStepResponseDto {
  private String gptResult;
  private int stepNumber;
  private String currentStepDescription;
}

