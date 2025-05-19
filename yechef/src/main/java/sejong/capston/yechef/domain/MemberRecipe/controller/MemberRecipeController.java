package sejong.capston.yechef.domain.MemberRecipe.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.service.RecipeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-recipes")
public class MemberRecipeController {

  private final RecipeService recipeService;

  @PatchMapping("/{memberId}/recipes/{recipeId}/like")
  public ResponseEntity<Void> toggleLike(
      @PathVariable Long memberId,
      @PathVariable Long recipeId
  ) {
    recipeService.toggleLike(memberId, recipeId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{memberId}/my-recipes")
  public List<RecipeDto> getMyRecipes(@PathVariable Long memberId) {
    return recipeService.getMyRecipes(memberId);
  }

  // 향후 확장을 위한 좋아요 목록 예시
  @GetMapping("/{memberId}/likes")
  public List<RecipeDto> getLikedRecipes(@PathVariable Long memberId) {
    return recipeService.getLikedRecipes(memberId); // 메서드 직접 구현 필요
  }
}


