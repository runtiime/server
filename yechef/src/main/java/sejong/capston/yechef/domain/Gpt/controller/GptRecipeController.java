package sejong.capston.yechef.domain.Gpt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.capston.yechef.domain.Gpt.dto.ChatGPTResponse;
import sejong.capston.yechef.domain.Recipe.dto.RecipeResponseDto;
import sejong.capston.yechef.domain.Gpt.service.GptRecipeService;
import sejong.capston.yechef.domain.Recipe.dto.SaveRecipeRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt/recipes")
@Tag(name = "", description = "동행 api 정보")
public class GptRecipeController {

    private final GptRecipeService gptRecipeService;

    @Operation(summary = "rawRecipe 텍스트로 GPT 파싱 후 레시피/재료/단계 일괄 저장")
    @PostMapping("/save")
    public RecipeResponseDto saveRecipe(
            @RequestParam Long memberId,
            @RequestParam String rawRecipe,
            @RequestBody SaveRecipeRequest request
    ) {
        return gptRecipeService.createFromRaw(memberId, rawRecipe, request);
    }
}
