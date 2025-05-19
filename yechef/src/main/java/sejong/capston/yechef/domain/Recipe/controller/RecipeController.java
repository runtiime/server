package sejong.capston.yechef.domain.Recipe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sejong.capston.yechef.domain.Recipe.dto.RecipeCreateDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.service.RecipeService;
import sejong.capston.yechef.domain.Member.dto.LoginMemberDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

  private final RecipeService recipeService;

  @PostMapping
  public RecipeDto createRecipe(
      @AuthenticationPrincipal LoginMemberDto user,
      @RequestBody RecipeCreateDto dto
  ) {
    return recipeService.create(user.getId(), dto);
  }

  @GetMapping("/{recipeId}")
  public RecipeDto getRecipe(@PathVariable Long recipeId) {
    return recipeService.getRecipe(recipeId);
  }

  @DeleteMapping("/{recipeId}")
  public ResponseEntity<Void> deleteRecipe(
      @AuthenticationPrincipal LoginMemberDto user,
      @PathVariable Long recipeId
  ) {
    recipeService.delete(user.getId(), recipeId);
    return ResponseEntity.noContent().build();
  }


}