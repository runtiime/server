package sejong.capston.yechef.domain.Gpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sejong.capston.yechef.domain.Gpt.dto.*;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GptService {
    private final RestTemplate restTemplate;
    @Value("${OPENAI_MODEL}")      private String    model;
    @Value("${OPENAI_API_URL}")    private String    apiUrl;
    private final ObjectMapper      objectMapper = new ObjectMapper();

    public IngredientAndRecipeDto parseRecipe(String rawRecipe) {
        // system 프롬프트: 출력 포맷을 JSON 스키마로 고정
        String systemPrompt = """
                You are a recipe‐parser bot.
                 ◆ INPUT : A free-form Korean recipe text that contains both ingredients and cooking instructions.
                 ◆ OUTPUT : (MUST be valid **pure JSON**, no markdown code-block, no extra keys) 
                 {
                   "ingredients": [
                     { "name": "<재료명>", "quantity": "<수량 또는 계량 단위>" },
                     …
                   ],
                   "steps": [
                     { "order": 1, "action": "<단일 행위>", "description": "<상세 설명(어떤 재료를 어떻게 하는지)>" },
                     …
                   ]
                 }
                 
                 ▣ Rules for steps 
                 1. Split the recipe into the smallest practical cooking actions.
                    - 한 줄에 여러 행동(자르고 볶는다)이 나오면 반드시 분리한다. 
                 2. action 값은 _짧은 한국어 동사/동사구_만 기재한다. (예: "떡 준비", "오뎅 자르기", "소세지 칼집", "계란 삶기", "우유 붓기", "6분 끓이기", "치즈 넣기")
                    - 명사·부가어구(“재료 손질”, “양념 준비”) 같은 포괄적 표현은 사용하지 않는다. 
                 3. description 에는 대상 재료·온도·시간 등 세부 정보를 풀어서 적는다. 
                 4. 번호(order)는 1부터 순차적으로 증가한다. 
                 5. 출력은 오직 JSON 하나! (앞뒤 설명, json 블록, 태그 모두 금지)
                
        """;

        // messages 준비
        List<Message> messages = List.of(
                new Message("system", systemPrompt),
                new Message("user", rawRecipe)
        );
        ChatGPTRequest req = new ChatGPTRequest(model, messages);
        ChatGPTResponse resp = restTemplate.postForObject(apiUrl, req, ChatGPTResponse.class);
        String content = resp.getChoices().get(0).getMessage().getContent().trim();

        // JsonNode로 파싱해서 DTO로 직접 매핑
        try {
            JsonNode root = objectMapper.readTree(content);

            // ingredients 배열 추출
            List<IngredientDto> ingredients = new ArrayList<>();
            for (JsonNode ingNode : root.path("ingredients")) {
                String name     = ingNode.path("name").asText("");
                String quantity = ingNode.path("quantity").asText("");
                ingredients.add(new IngredientDto(name, quantity));
            }

            // steps 배열 추출
            List<RecipeStepDto> steps = new ArrayList<>();
            for (JsonNode stepNode : root.path("steps")) {
                int    order       = stepNode.path("order").asInt(0);
                String action      = stepNode.path("action").asText("");
                String description = stepNode.path("description").asText("");
                steps.add(RecipeStepDto.builder()
                        .stepNumber(order)
                        .action(action)
                        .description(description)
                        .build()
                );
            }

            return new IngredientAndRecipeDto(ingredients, steps);

        } catch (JsonProcessingException e) {
            log.error("GPT 응답 내용을 파싱하는 데 실패했습니다.: {}", content, e);
            throw BaseException.from(ErrorCode.GPT_RESPONSE_PARSING_FAILED);
        }
    }

    public String simplePrompt(String prompt) {
        List<Message> messages = List.of(
            new Message("system", "너는 요리 진행 어시스턴트다. 사용자 음성을 해석해 다음 행동을 판단한다."),
            new Message("user", prompt)
        );
        ChatGPTRequest req = new ChatGPTRequest(model, messages);
        ChatGPTResponse resp = restTemplate.postForObject(apiUrl, req, ChatGPTResponse.class);
        return resp.getChoices().get(0).getMessage().getContent().trim();
    }

}

