package sejong.capston.yechef.domain.Gpt.dto;

import lombok.*;
import sejong.capston.yechef.domain.Ingredient.Ingredient;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDto {
    private String name;
    private String quantity;

    public static IngredientDto from(Ingredient ingredient) {
        return IngredientDto.builder()
                .name(ingredient.getOriginalName())
                .quantity(ingredient.getOriginalAmount())
                .build();
    }
}
