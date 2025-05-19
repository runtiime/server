package sejong.capston.yechef.domain.Recipe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeProgressService {

  private final RecipeRepository recipeRepository;
  private final RecipeStepRepository recipeStepRepository;
  private final IngredientRepository ingredientRepository;
  private final GptService gptService;

  public RecipeStepDto processStep(Long recipeId, int stepNumber, String userInput) {
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

    RecipeStep currentStep = recipeStepRepository.findByRecipeAndStepNumber(recipe, stepNumber)
        .orElseThrow(() -> BaseException.from(ErrorCode.NOT_EXIST_THIS_STEP));

    List<Ingredient> ingredients = ingredientRepository.findByRecipe(recipe);

    log.info("🎙️ 사용자 입력: {}", userInput);
    log.info("📍 현재 단계 번호: {}, 내용: {}", stepNumber, currentStep.getDescription());

    String gptPrompt = buildPrompt(currentStep.getDescription(), userInput, ingredients);
    String gptResult = gptService.simplePrompt(gptPrompt);

    log.info("🧠 GPT 응답: {}", gptResult);

    if (gptResult.equalsIgnoreCase("NEXT")) {
      Optional<RecipeStep> nextStepOpt = recipeStepRepository.findByRecipeAndStepNumber(recipe, stepNumber + 1);
      if (nextStepOpt.isPresent()) {
        RecipeStep nextStep = nextStepOpt.get();
        return RecipeStepDto.builder()
            .stepNumber(stepNumber + 1)
            .description(nextStep.getDescription())
            .action("다음으로 갈게요.")
            .build();
      } else {
        return RecipeStepDto.builder()
            .stepNumber(stepNumber)
            .description(currentStep.getDescription())
            .action("마지막 단계예요.")
            .build();
      }

    } else if (gptResult.equalsIgnoreCase("PREVIOUS")) {
      if (stepNumber <= 1) {
        return RecipeStepDto.builder()
            .stepNumber(stepNumber)
            .description(currentStep.getDescription())
            .action("처음 단계예요.")
            .build();
      } else {
        RecipeStep prevStep = recipeStepRepository.findByRecipeAndStepNumber(recipe, stepNumber - 1)
            .orElseThrow(() -> BaseException.from(ErrorCode.NOT_EXIST_THIS_STEP));
        return RecipeStepDto.builder()
            .stepNumber(stepNumber - 1)
            .description(prevStep.getDescription())
            .action("이전 단계로 갈게요.")
            .build();
      }

    } else if (gptResult.equalsIgnoreCase("REPEAT")) {
      return RecipeStepDto.builder()
          .stepNumber(stepNumber)
          .description(currentStep.getDescription())
          .action("다시 말씀드릴게요.")
          .build();

    } else if (gptResult.startsWith("EXPLAIN:")) {
      String explanation = gptResult.replace("EXPLAIN:", "").trim();
      return RecipeStepDto.builder()
          .stepNumber(stepNumber)
          .description(currentStep.getDescription())
          .action("설명드릴게요. " + explanation)
          .build();

    } else if (gptResult.startsWith("SUBSTITUTE:")) {
      String[] parts = gptResult.replace("SUBSTITUTE:", "").trim().split("→");
      if (parts.length == 2) {
        String original = parts[0].trim();
        String substitute = parts[1].trim();

        ingredientRepository.findByRecipe(recipe).stream()
            .filter(i -> i.getOriginalName().equals(original))
            .findFirst()
            .ifPresent(i -> i.setOriginalName(substitute)); // 실제 반영

        return RecipeStepDto.builder()
            .stepNumber(stepNumber)
            .description(currentStep.getDescription())
            .action("‘%s’를 ‘%s’로 바꿀게요.".formatted(original, substitute))
            .build();
      }
    }

    return RecipeStepDto.builder()
        .stepNumber(stepNumber)
        .description(currentStep.getDescription())
        .action("다시 말씀해 주세요.")
        .build();
  }

  private String buildPrompt(String stepDescription, String userInput, List<Ingredient> ingredients) {
    String formattedIngredients = ingredients.stream()
        .map(i -> "- %s (%s)".formatted(i.getOriginalName(), i.getOriginalAmount()))
        .collect(Collectors.joining("\n"));

    return """
    지금 요리 중인 단계는 "%s"입니다.
    사용자 입력: "%s"

    참고할 재료 목록:
    %s

    아래 중 하나로 답해주세요:
    - "NEXT": 다음 단계로 이동
    - "PREVIOUS": 이전 단계로 이동
    - "REPEAT": 다시 설명
    - "EXPLAIN: <설명>": 보조 설명
    - "SUBSTITUTE: <기존재료> → <대체재료>": 재료 변경
    """.formatted(stepDescription, userInput, formattedIngredients);
  }
}