package sejong.capston.yechef.domain.Recipe.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeCreateDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.service.RecipeService;
import sejong.capston.yechef.domain.Member.dto.LoginMemberDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

  private final RecipeService recipeService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public RecipeDto createRecipe(
      @AuthenticationPrincipal LoginMemberDto user,
      @RequestPart("data") RecipeCreateDto dto,
      @RequestPart("sourceImage") MultipartFile sourceImageFile
  ) {
    return recipeService.create(user.getId(), dto, sourceImageFile);
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

  @GetMapping("/public")
  public ResponseEntity<List<Recipe>> getPublicRecipes() {
    List<Recipe> publicRecipes = recipeService.getPublicRecipes();
    return ResponseEntity.ok(publicRecipes);
  }
}