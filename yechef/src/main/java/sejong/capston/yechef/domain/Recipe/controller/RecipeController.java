package sejong.capston.yechef.domain.Recipe.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;
import sejong.capston.yechef.domain.Recipe.dto.DetailRecipeDto;
import sejong.capston.yechef.domain.Recipe.dto.OcrRecipeResultDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.service.RecipeService;

@Tag(name = "레시피 API", description = "레시피 생성·조회·삭제·공개목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

  private final RecipeService recipeService;

  // 레시피 저장 (AI에서 받은 OCR 결과 저장)
  @PostMapping(value = "/{memberId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RecipeDto> createRecipe(
      @Parameter(description = "회원 ID", required = true)
      @PathVariable("memberId") Long memberId,

      @RequestBody RecipeParseResultDto dto
  ) {
    RecipeDto result = recipeService.create(memberId, dto);
    return ResponseEntity
        .created(URI.create("/api/recipes/" + result.getId()))
        .body(result);
  }

  //  @PostMapping(value = "/ocr/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//  public ResponseEntity<DetailRecipeDto> createRecipeFromImage(
//      @RequestParam("memberId") Long memberId,
//      @RequestPart("image") MultipartFile imageFile) {
//    DetailRecipeDto result = recipeService.createRecipeFromImage(memberId, imageFile);
//    return ResponseEntity.ok(result);
//  }
  @PostMapping(value = "/ocr/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<OcrRecipeResultDto> createRecipeFromImage(
      @RequestParam("memberId") Long memberId,
      @RequestPart("image") MultipartFile imageFile) {

    OcrRecipeResultDto result = recipeService.createRecipeFromImage(memberId, imageFile);

    return ResponseEntity.ok(result);
  }

  // 상세 조회
  @GetMapping("/{recipeId}")
  public ResponseEntity<DetailRecipeDto> getRecipe(
      @PathVariable("recipeId") Long recipeId,
      @RequestParam(name = "targetServings", required = false) Integer targetServings
  ) {
    DetailRecipeDto dto;
    if (targetServings != null) {
      // targetServings이 지정됐으면 스케일된 버전
      dto = recipeService.getScaledRecipe(recipeId, targetServings);
    } else {
      // 지정이 없으면 기존 로직
      dto = recipeService.getRecipe(recipeId);
    }
    return ResponseEntity.ok(dto);
  }


  // 삭제
  @DeleteMapping("/members/{memberId}/recipes/{recipeId}")
  public ResponseEntity<Void> deleteRecipe(
      @PathVariable("memberId") Long memberId,
      @PathVariable("recipeId") Long recipeId
  ) {
    recipeService.delete(memberId, recipeId);
    return ResponseEntity.noContent().build();
  }

  // 공개 목록 조회
  @GetMapping("/public")
  public ResponseEntity<List<RecipeDto>> getPublicRecipes() {
    return ResponseEntity.ok(recipeService.getPublicRecipes());
  }
}