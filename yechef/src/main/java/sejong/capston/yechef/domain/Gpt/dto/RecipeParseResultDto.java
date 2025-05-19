package sejong.capston.yechef.domain.Gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeParseResultDto {
    private String title;
    private List<IngredientDto> ingredients;
    private List<RecipeStepDto> steps;
}
