package sejong.capston.yechef.domain.RecipeSteps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.capston.yechef.domain.RecipeSteps.dto.RecipeStepDetailDto;
import sejong.capston.yechef.domain.RecipeSteps.service.RecipeStepService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipesteps")
public class RecipeStepController {

    private final RecipeStepService recipeStepService;

    @GetMapping("/{recipeId}/{stepNumber}")
    public RecipeStepDetailDto getParsedStep(
            @PathVariable Long recipeId,
            @PathVariable int stepNumber
    ) {
        return recipeStepService.getParsedRecipeStep(recipeId, stepNumber);
    }
}
