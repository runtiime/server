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

    public RecipeParseResultDto parseRecipe(String rawRecipe) {
        String systemPrompt = """
        너는 레시피 파서를 수행하는 AI야.
        
        ◆ 입력: 재료와 요리 과정을 포함한 자유 형식의 한국어 요리 레시피 텍스트
        ◆ 출력: 아래 JSON 형식에 맞춘 **순수 JSON 데이터만** 출력 (마크다운, 설명 문구, 주석 모두 금지)
        
        {
          "title": "<요리명>",       ← 제목이 명시되어 있지 않으면, 재료와 과정을 참고하여 가장 기본적인 음식 이름으로 추정해서 작성하세요.
          "ingredients": [
            { "name": "<재료명>", "quantity": "<수량 또는 계량 단위>" },
            …
          ],
          "steps": [
            { "stepNumber": 1, "action": "<단일 행위>", "description": "<상세 설명>" },
            …
          ]
        }
        
        ▣ 출력 규칙
        1. steps는 문장을 가장 작은 단위의 실제 행동 단위로 쪼개서 작성합니다. (예: "자르고 볶는다" → "자르기", "볶기"로 나누기)
        2. action 값은 짧은 한국어 동사 또는 동사구여야 합니다. (예: "떡 준비", "칼집 넣기")
        3. description에는 어떤 재료를 어떻게 다루는지, 시간, 온도 등 구체 정보를 포함합니다.
        4. 출력은 반드시 JSON만 포함되도록 하세요. 설명이나 마크다운 블록 없이 반환합니다.
        
        반드시 지켜야할 것
        "steps" 내부의 "stepNumber" 키 값의 value는 제일 처음 생성되는 값이 1이며,
        1부터 시작하여 순차적으로 1씩 무조건 !! 증가합니다. 절대 0 이 될 수 없습니다.(ex. 1,2,3 ...)
        "steps": [
            { "stepNumber": 1, "action": "<단일 행위>", "description": "<상세 설명>" },
            { "stepNumber": 2, "action": "<단일 행위>", "description": "<상세 설명>" },
            { "stepNumber": 3, "action": "<단일 행위>", "description": "<상세 설명>" },
            …
            …
          ]
        """;

        // GPT 호출
        List<sejong.capston.yechef.domain.Gpt.dto.Message> messages = List.of(
                new sejong.capston.yechef.domain.Gpt.dto.Message("system", systemPrompt),
                new sejong.capston.yechef.domain.Gpt.dto.Message("user", rawRecipe)
        );
        ChatGPTRequest req = new ChatGPTRequest(model, messages);
        ChatGPTResponse resp = restTemplate.postForObject(apiUrl, req, ChatGPTResponse.class);
        String content = resp.getChoices().get(0).getMessage().getContent().trim();

        try {
            JsonNode root = objectMapper.readTree(content);

            // 제목
            String title = root.path("title").asText("");

            // 재료
            List<IngredientDto> ingredients = new ArrayList<>();
            for (JsonNode ingNode : root.path("ingredients")) {
                ingredients.add(new IngredientDto(
                        ingNode.path("name").asText(""),
                        ingNode.path("quantity").asText("")
                ));
            }

            // 단계
            List<RecipeStepDto> steps = new ArrayList<>();
            for (JsonNode stepNode : root.path("steps")) {
                steps.add(RecipeStepDto.builder()
                        .stepNumber(stepNode.path("order").asInt(0))
                        .action(stepNode.path("action").asText(""))
                        .description(stepNode.path("description").asText(""))
                        .build()
                );
            }

            return new RecipeParseResultDto(title, ingredients, steps);

        } catch (JsonProcessingException e) {
            log.error("GPT 응답 파싱 실패 → content: {}", content, e);
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

