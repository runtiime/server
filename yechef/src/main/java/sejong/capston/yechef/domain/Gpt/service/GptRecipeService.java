package sejong.capston.yechef.domain.Gpt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.Gpt.dto.IngredientAndRecipeDto;
import sejong.capston.yechef.domain.Ingredient.Ingredient;
import sejong.capston.yechef.domain.Ingredient.repository.IngredientRepository;
import sejong.capston.yechef.domain.Ingredient.service.IngredientService;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Member.repository.MemberRepository;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.MemberRecipe.repository.MemberRecipeRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeResponseDto;
import sejong.capston.yechef.domain.Recipe.dto.SaveRecipeRequest;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;
import sejong.capston.yechef.domain.RecipeSteps.repository.RecipeStepRepository;
import sejong.capston.yechef.domain.RecipeSteps.service.RecipeStepService;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class GptRecipeService {

    private final GptService gptService;
    private final IngredientService ingredientService;
    private final RecipeStepService recipeStepService;

    private final RecipeRepository recipeRepository;
    private final MemberRepository memberRepository;
    private final MemberRecipeRepository memberRecipeRepository;

    @Transactional
    public RecipeResponseDto createFromRaw(Long memberId, String rawRecipe, SaveRecipeRequest request) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

        // GPT로 파싱
        IngredientAndRecipeDto recipeDtoToSave = gptService.parseRecipe(rawRecipe);

        // Recipe 엔티티 저장
        Recipe recipe = Recipe.of(request.getTitle(), member.getNickname(), request.getRecipeType());
        recipeRepository.save(recipe);

        // MemberRecipe 연결 저장
        MemberRecipe memberRecipe = new MemberRecipe(member, recipe);
        memberRecipeRepository.save(memberRecipe);

        // Ingredient 저장
        ingredientService.saveIngredients(recipeDtoToSave.getIngredients(), recipe);

        // RecipeStep 저장
        recipeStepService.saveSteps(recipeDtoToSave.getSteps(), recipe);

        return RecipeResponseDto.from(recipe);
    }
}
