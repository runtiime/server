package sejong.capston.yechef.domain.Ingredient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.Gpt.dto.IngredientDto;
import sejong.capston.yechef.domain.Ingredient.Ingredient;
import sejong.capston.yechef.domain.Ingredient.repository.IngredientRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    @Transactional
    public void saveIngredients(List<IngredientDto> dtos, Recipe recipe) {
        for (var dto : dtos) {
            Ingredient ing = Ingredient.of(
                    dto.getName(),
                    null,                  // alternativeName
                    dto.getQuantity(),
                    null,                  // oneServingAmount
                    recipe
            );
            ingredientRepository.save(ing);
        }
    }
}
