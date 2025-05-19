package sejong.capston.yechef.domain.Recipe.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.capston.yechef.domain.Recipe.dto.RecipeCreateDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.service.RecipeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/recipes")
public class RecipeController {

  private final RecipeService recipeService;

  @PostMapping
  public RecipeDto createRecipe(
      @PathVariable Long memberId,
      @RequestBody RecipeCreateDto dto
  ) {
    return recipeService.create(memberId, dto);
  }

  @GetMapping
  public List<RecipeDto> getMyRecipes(@PathVariable Long memberId) {
    return recipeService.getMyRecipes(memberId);
  }

  @GetMapping("/{recipeId}")
  public RecipeDto getRecipe(@PathVariable Long memberId, @PathVariable Long recipeId) {
    return recipeService.getRecipe(recipeId);
  }

  @DeleteMapping("/{recipeId}")
  public ResponseEntity<Void> deleteRecipe(@PathVariable Long memberId, @PathVariable Long recipeId) {
    recipeService.delete(memberId, recipeId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{recipeId}/like")
  public ResponseEntity<Void> toggleLike(@PathVariable Long memberId, @PathVariable Long recipeId) {
    recipeService.toggleLike(memberId, recipeId);
    return ResponseEntity.ok().build();
  }
}
