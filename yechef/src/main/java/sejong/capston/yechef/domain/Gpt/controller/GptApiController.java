package sejong.capston.yechef.domain.Gpt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;
import sejong.capston.yechef.domain.Gpt.service.GptService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bot")
public class GptApiController {

    private final GptService gptService;

    @Value("${OPENAI_MODEL}")
    private String model;

    @Value("${OPENAI_API_URL}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @GetMapping("/parseRecipe")
    public RecipeParseResultDto parseRecipe(@RequestParam("recipe") String rawRecipe) {
        return gptService.parseRecipe(rawRecipe);
    }
}