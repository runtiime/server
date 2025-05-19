package sejong.capston.yechef.domain.RecipeSteps.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;
import sejong.capston.yechef.domain.RecipeSteps.repository.RecipeStepRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeStepService {
    private final RecipeStepRepository recipeStepRepository;

    @Transactional
    public void saveSteps(List<RecipeStepDto> recipeStepDtos, Recipe recipe) {
        for (var dto : recipeStepDtos) {
            RecipeStep step = RecipeStep.of(
                    dto.getStepNumber(),
                    dto.getDescription(),
                    recipe
            );
            recipeStepRepository.save(step);
        }
    }
}
