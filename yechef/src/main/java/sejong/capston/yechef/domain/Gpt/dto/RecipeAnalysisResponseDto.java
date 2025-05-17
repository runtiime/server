package sejong.capston.yechef.domain.Gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeAnalysisResponseDto {
    private List<IngredientDto> ingredients;
    private List<RecipeStepDto> steps;
}
