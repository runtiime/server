package sejong.capston.yechef.domain.Recipe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejong.capston.yechef.domain.Gpt.service.GptService;
import sejong.capston.yechef.domain.Ingredient.Ingredient;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.domain.Ingredient.repository.IngredientRepository;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
import sejong.capston.yechef.domain.RecipeSteps.repository.RecipeStepRepository;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeProgressService {

  private final RecipeRepository recipeRepository;
  private final RecipeStepRepository recipeStepRepository;
  private final IngredientRepository ingredientRepository;
  private final GptService gptService;

  public RecipeStepDto processStep(Long recipeId, int stepNumber, String userInput) {
    // 레시피 & 단계 조회
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

    RecipeStep currentStep = recipeStepRepository.findByRecipeAndStepNumber(recipe, stepNumber)
        .orElseThrow(() -> BaseException.from(ErrorCode.NOT_EXIST_THIS_STEP));

    // 재료 조회
    List<Ingredient> ingredients = ingredientRepository.findByRecipe(recipe);

    // GPT 프롬프트 구성 및 호출
    String gptPrompt = buildPrompt(currentStep.getDescription(), userInput, ingredients);
    String gptResult = gptService.simplePrompt(gptPrompt);

    return RecipeStepDto.builder()
        .action(gptResult)
        .stepNumber(stepNumber)
        .description(currentStep.getDescription())
        .build();
  }

  private String buildPrompt(String stepDescription, String userInput, List<Ingredient> ingredients) {
    String formattedIngredients = ingredients.stream()
        .map(i -> "- %s (%s)".formatted(i.getOriginalName(), i.getOriginalAmount()))
        .collect(Collectors.joining("\n"));

    return """
      현재 요리 단계: "%s"
      사용자의 음성 입력: "%s"

      레시피 재료 목록:
      %s

      아래 중 하나만 출력하세요:
      - "NEXT": 다음 단계로 넘어갈 수 있음
      - "REPEAT": 다시 설명 필요
      - "EXPLAIN: <설명>": 보조 설명 필요
      """.formatted(stepDescription, userInput, formattedIngredients);
  }
}
