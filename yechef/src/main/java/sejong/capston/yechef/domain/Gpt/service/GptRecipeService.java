package sejong.capston.yechef.domain.Gpt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.Image.service.ImageService;
import sejong.capston.yechef.domain.Ingredient.service.IngredientService;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Member.repository.MemberRepository;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.MemberRecipe.repository.MemberRecipeRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeResponseDto;
import sejong.capston.yechef.domain.Recipe.dto.SaveRecipeRequest;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
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
    private final ImageService imageService; // ✅ 썸네일 생성용

    @Transactional
    public RecipeResponseDto saveMyNewRecipe(
        Long memberId,
        SaveRecipeRequest request
    ) {
        // 1. 회원 확인
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));
        // Recipe 저장
        Recipe recipe = Recipe.of(request.getTitle(), member.getNickname(), Recipe.RecipeType.PRIVATE,
                request.getServings());
        recipeRepository.save(recipe);

        // 3. 썸네일 자동 생성
        imageService.generateAndSaveThumbnail(recipe.getId());

        // 4. MemberRecipe 연결
        MemberRecipe memberRecipe = MemberRecipe.of(member, recipe);
        memberRecipeRepository.save(memberRecipe);

        // 5. Ingredient / RecipeStep 저장
        ingredientService.saveIngredients(request.getIngredients(), recipe);
        recipeStepService.saveSteps(request.getSteps(), recipe);

        return RecipeResponseDto.from(recipe);
    }
}