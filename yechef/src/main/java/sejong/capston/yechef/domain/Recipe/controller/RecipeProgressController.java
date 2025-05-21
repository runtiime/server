package sejong.capston.yechef.domain.Recipe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.domain.Recipe.dto.VoiceInputDto;
import sejong.capston.yechef.domain.Recipe.service.RecipeProgressService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/recipes")
public class RecipeProgressController {

  private final RecipeProgressService progressService;

  @GetMapping("/{recipeId}/step/{stepNumber}/progress")
  public RecipeStepDto progressStep(
      @RequestParam("memberId") Long memberId,
      @PathVariable("recipeId") Long recipeId,
      @PathVariable("stepNumber") int stepNumber,
      @RequestParam("text") String inputText
  ) {
    return progressService.processStep(memberId, recipeId, stepNumber, inputText);
  }

}

