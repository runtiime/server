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
    private String text;    // 음식 한 줄 설명
    private int servings;
    private List<IngredientDto> ingredients;
    private List<RecipeStepDto> steps;

    private String sourceImageUrl;

    public RecipeParseResultDto(    // TODO: 사용처를 빌더 패턴으로 수정하기
        String title,
        String text,
        int servings,
        List<IngredientDto> ingredients,
        List<RecipeStepDto> steps
    ) {
        this.title = title;
        this.text = text;
        this.servings = servings;
        this.ingredients = ingredients;
        this.steps = steps;
    }
}
