package sejong.capston.yechef.domain.RecipeSteps.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.Gpt.service.GptService;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;
import sejong.capston.yechef.domain.RecipeSteps.dto.RecipeStepDetailDto;
import sejong.capston.yechef.domain.RecipeSteps.repository.RecipeStepRepository;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeStepService {

    private final RecipeStepRepository recipeStepRepository;
    private final RecipeRepository recipeRepository;

    private final GptService gptService;

    @Transactional
    public void saveSteps(List<RecipeStepDetailDto> recipeStepDetailDtos, Recipe recipe) {
        for (var dto : recipeStepDetailDtos) {
                RecipeStep step = RecipeStep.of(
                        dto.getStepNumber(),
                        dto.getAction(),
                        dto.getIngredients(),
                        dto.getDescription(),
                        recipe
                );
                recipeStepRepository.save(step);
        }
    }

    @Transactional(readOnly = true)
    public RecipeStepDetailDto getRecipeStep(Long recipeId, int stepNumber) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

        return recipe.getRecipeSteps().stream()
                .filter(s -> s.getStepNumber() == stepNumber)
                .findFirst()
                .map(RecipeStepDetailDto::from)
                .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));
    }

    @Transactional(readOnly = true)
    public RecipeStepDetailDto getParsedRecipeStep(Long recipeId, int stepNumber) {
        RecipeStepDetailDto dto = getRecipeStep(recipeId, stepNumber);
        return gptService.parseRecipeSteps(recipeId, stepNumber);
    }
}
