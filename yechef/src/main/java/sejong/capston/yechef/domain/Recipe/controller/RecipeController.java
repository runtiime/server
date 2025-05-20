package sejong.capston.yechef.domain.Recipe.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.service.RecipeService;

@Tag(name = "레시피 API", description = "레시피 생성·조회·삭제·공개목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

  private final RecipeService recipeService;
  @PostMapping(value = "/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<RecipeDto> createRecipe(
          @Parameter(description = "회원 ID", required = true)
          @PathVariable Long memberId,

          @Parameter(description = "GPT 파싱 결과 JSON", required = true)
          @RequestPart("dto") RecipeParseResultDto dto,

          @Parameter(description = "레시피 이미지 파일", required = true)
          @RequestPart("sourceImageFile") MultipartFile sourceImageFile
  ) {
    RecipeDto result = recipeService.create(memberId, dto, sourceImageFile);
    return ResponseEntity
            .created(URI.create("/api/recipes/" + result.getId()))
            .body(result);
  }

  @GetMapping("/{recipeId}")
  public ResponseEntity<RecipeDto> getRecipe(
          @Parameter(description = "조회할 레시피 ID", required = true)
          @PathVariable Long recipeId
  ) {
    return ResponseEntity.ok(recipeService.getRecipe(recipeId));
  }

  @DeleteMapping("/{recipeId}")
  public ResponseEntity<Void> deleteRecipe(
          @Parameter(description = "사용자 ID (테스트용)", required = true)
          @RequestParam Long memberId,

          @Parameter(description = "삭제할 레시피 ID", required = true)
          @PathVariable Long recipeId
  ) {
    recipeService.delete(memberId, recipeId);
    return ResponseEntity.noContent().build();
  }
  
  @GetMapping("/public")
  public ResponseEntity<List<Recipe>> getPublicRecipes() {
    return ResponseEntity.ok(recipeService.getPublicRecipes());
  }
}
