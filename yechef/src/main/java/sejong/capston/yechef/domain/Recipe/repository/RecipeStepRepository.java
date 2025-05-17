package sejong.capston.yechef.domain.Recipe.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
  Optional<RecipeStep> findByRecipeAndStepNumber(Recipe recipe, int stepNumber);
}
