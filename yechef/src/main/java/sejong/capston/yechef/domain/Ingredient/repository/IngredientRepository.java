package sejong.capston.yechef.domain.Ingredient.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sejong.capston.yechef.domain.Ingredient.Ingredient;
import sejong.capston.yechef.domain.Recipe.Recipe;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
  List<Ingredient> findByRecipe(Recipe recipe);
}