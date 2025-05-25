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
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;
import sejong.capston.yechef.domain.RecipeSteps.dto.RecipeStepDetailDto;
import sejong.capston.yechef.domain.RecipeSteps.repository.RecipeStepRepository;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GptService {
    private final RestTemplate restTemplate;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeRepository recipeRepository;
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
          "text": "<한 줄 요리 설명>",  ← 한 줄로 요리에 대한 간단한 설명 넣어줘
          "servings": "<인분 수>",   ← 몇 인분인지 반드시 작성(기본 값은 1인분, 레시피 텍스트 내용 기반으로 추측)
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
        5. steps 배열의 각 항목에는 "stepNumber" 필드가 반드시 있어야 하며, 그 값은 **1부터 시작해 1씩 순차 증가**해야 합니다. 
        절대로 0이거나 중복되면 안 됩니다. (예: 1, 2, 3, …)
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

            // 음식 한 줄 설명
            String text = root.path("text").asText("");

            // 인분
            int servings = root.path("servings").asInt(1);

            // 재료
            List<IngredientDto> ingredients = new ArrayList<>();
            for (JsonNode ingNode : root.path("ingredients")) {
                ingredients.add(new IngredientDto(
                        ingNode.path("name").asText(""),
                        ingNode.path("quantity").asText("")
                ));
            }

            List<RecipeStepDetailDto> steps = new ArrayList<>();
            int index = 1;
            for (JsonNode stepNode : root.path("steps")) {
                steps.add(RecipeStepDetailDto.builder()
                        .stepNumber(stepNode.path("order").asInt(index++))
                        .action(stepNode.path("action").asText(""))
                        .description(stepNode.path("description").asText(""))
                        .build()
                );
            }

            return new RecipeParseResultDto(title, text, servings, ingredients, steps);

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

    public RecipeStepDetailDto parseRecipeSteps(Long recipeId, int stepNumber) {
        String systemPrompt = """
        당신은 단일 한국어 조리 단계 설명을 받아 JSON 객체로 변환하는 AI입니다.
        ◆ 입력: 한국어로 된 하나의 조리 단계 설명 문장
        ◆ 출력: 순수 JSON만 반환하며, 반드시 아래 세 가지 필드만 포함해야 합니다.
          - "stepNumber": 원래 단계 번호 (정수)
          - "action": 해당 조리 동작을 짧은 한국어 동사구(예: "썰기", "볶기")로 표현
          - "ingredients": 설명에 언급된 재료명만 한국어로 배열에 담아 반환  
             (수식어 제거, "물" 제외)
        그 외의 키, 주석, 설명 문구는 절대 포함하지 마세요!
        """;

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

        RecipeStep recipeStep = recipeStepRepository.findByRecipeAndStepNumber(recipe, stepNumber)
                .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

        RecipeStepDetailDto dto = new RecipeStepDetailDto(recipeStep.getStepNumber(),
                recipeStep.getAction(), recipeStep.getDescription(), recipeStep.getIngredients());

        List<Message> messages = List.of(
                new Message("system", systemPrompt),
                new Message("user", dto.getDescription())
        );
        ChatGPTRequest req = new ChatGPTRequest(model, messages);
        ChatGPTResponse resp = restTemplate.postForObject(apiUrl, req, ChatGPTResponse.class);
        String content = resp.getChoices().get(0).getMessage().getContent().trim();

        try {
            JsonNode root = objectMapper.readTree(content);
            int num = root.path("stepNumber").asInt(dto.getStepNumber());
            String action = root.path("action").asText();
            List<String> ingredients = new ArrayList<>();

            for (JsonNode ing : root.path("ingredients")) {
                ingredients.add(ing.asText());
            }

            return RecipeStepDetailDto.of(num, action, dto.getDescription(), ingredients);

        } catch (JsonProcessingException e) {
            throw BaseException.from(ErrorCode.GPT_RESPONSE_PARSING_FAILED);
        }
    }

    public String parseRecipeStepsTest(Long recipeId, int stepNumber) {
        String systemPrompt = """
        당신은 단일 한국어 조리 단계 설명을 받아 JSON 객체로 변환하는 AI입니다.
        ◆ 입력: 한국어로 된 하나의 조리 단계 설명 문장
        ◆ 출력: 순수 JSON만 반환하며, 반드시 아래 세 가지 필드만 포함해야 합니다.
          - "stepNumber": 원래 단계 번호 (정수)
          - "action": 해당 조리 동작을 짧은 한국어 동사구(예: "썰기", "볶기")로 표현
          - "ingredients": 설명에 언급된 재료명만 한국어로 배열에 담아 반환  
             (수식어 제거, "물" 제외)
        그 외의 키, 주석, 설명 문구는 절대 포함하지 마세요!
        """;


        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

        RecipeStep recipeStep = recipeStepRepository.findByRecipeAndStepNumber(recipe, stepNumber)
                .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

        RecipeStepDetailDto dto = new RecipeStepDetailDto(recipeStep.getStepNumber(),
                recipeStep.getAction(), recipeStep.getDescription(), recipeStep.getIngredients());

        List<Message> messages = List.of(
                new Message("system", systemPrompt),
                new Message("user", dto.getDescription())
        );
        ChatGPTRequest req = new ChatGPTRequest(model, messages);
        ChatGPTResponse resp = restTemplate.postForObject(apiUrl, req, ChatGPTResponse.class);
        String content = resp.getChoices().get(0).getMessage().getContent().trim();
        return content;
    }
}

