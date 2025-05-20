package sejong.capston.yechef.domain.Recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sejong.capston.yechef.domain.Gpt.dto.IngredientDto;
import sejong.capston.yechef.domain.Recipe.Recipe;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveRecipeRequest {
    private String title;
    private int servings;
    private List<IngredientDto> ingredients;
    private List<RecipeStepDto> steps;
}
