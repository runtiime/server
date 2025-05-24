package sejong.capston.yechef.domain.Recipe.dto;

import lombok.*;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStepDto {
    private int stepNumber;        // 1,2,3,…
    private String action;      // ex) “떡 자르기”
    private String description; // “떡 300g을 먹기 좋은 크기로 썬다”
    private List<String> ingredients;

    public static RecipeStepDto from(RecipeStep step) {
        return RecipeStepDto.builder()
            .stepNumber(step.getStepNumber())
            .action(step.getAction())
            .description(step.getDescription())
            .ingredients(step.getIngredients())
            .build();
    }
}
