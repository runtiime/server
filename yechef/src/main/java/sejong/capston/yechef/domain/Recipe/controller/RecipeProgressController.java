package sejong.capston.yechef.domain.Recipe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.domain.Recipe.dto.VoiceInputDto;
import sejong.capston.yechef.domain.Recipe.service.RecipeProgressService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/recipes")
public class RecipeProgressController {

  private final RecipeProgressService progressService;

  @PostMapping("/{recipeId}/step/{stepNumber}/progress")
  public RecipeStepDto progressStep(
      @PathVariable("memberId") Long memberId,
      @PathVariable("recipeId") Long recipeId,
      @PathVariable("stepNumber") int stepNumber,
      @RequestBody VoiceInputDto input
  ) {
    return progressService.processStep(memberId, recipeId, stepNumber, input.getText());
  }
}

