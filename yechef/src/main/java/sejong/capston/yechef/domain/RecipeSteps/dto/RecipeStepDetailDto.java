package sejong.capston.yechef.domain.RecipeSteps.dto;

import lombok.*;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStepDetailDto {
    private int stepNumber;        // 1,2,3,…
    private String action;      // ex) “떡 자르기”
    private String description; // “떡 300g을 먹기 좋은 크기로 썬다”
    private List<String> ingredients;

    public RecipeStepDetailDto(int stepNumber, String action, String description) {
        this.stepNumber = stepNumber;
        this.action = action;
        this.description = description;
    }

    public static RecipeStepDetailDto from(RecipeStep step) {
        return RecipeStepDetailDto.builder()
                .stepNumber(step.getStepNumber())
                .description(step.getDescription())
                .build();
    }

    public static RecipeStepDetailDto of(int stepNumber, String action, String description, List<String> ingredients) {
        return RecipeStepDetailDto.builder()
                .stepNumber(stepNumber)
                .action(action)
                .description(description)
                .ingredients(ingredients)
                .build();
    }
}
