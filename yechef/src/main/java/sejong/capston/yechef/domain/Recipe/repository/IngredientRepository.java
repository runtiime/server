package sejong.capston.yechef.domain.Recipe.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sejong.capston.yechef.domain.Ingredients.Ingredient;
import sejong.capston.yechef.domain.Recipe.Recipe;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
  List<Ingredient> findByRecipe(Recipe recipe);
}