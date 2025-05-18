package sejong.capston.yechef.domain.Recipe.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Member.repository.MemberRepository;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipeRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeCreateDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;

@Service
@RequiredArgsConstructor
public class RecipeService {

  private final RecipeRepository recipeRepository;
  private final MemberRepository memberRepository;
  private final MemberRecipeRepository memberRecipeRepository;

  @Transactional
  public RecipeDto create(Long memberId, RecipeCreateDto dto) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

    Recipe recipe = Recipe.builder()
        .title(dto.getTitle())
        .author(member.getNickname())
        .likeCount(0)
        .ranking(0.0)
        .recipeType(dto.getRecipeType())
        .isUpdated(false)
        .build();
    recipeRepository.save(recipe);

    MemberRecipe memberRecipe = new MemberRecipe(member, recipe);
    memberRecipeRepository.save(memberRecipe);

    return RecipeDto.from(recipe);
  }

  @Transactional(readOnly = true)
  public List<RecipeDto> getMyRecipes(Long memberId) {
    List<Recipe> recipes = recipeRepository.findByMemberId(memberId);
    return recipes.stream().map(RecipeDto::from).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public RecipeDto getRecipe(Long recipeId) {
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> new EntityNotFoundException("레시피가 존재하지 않습니다."));
    return RecipeDto.from(recipe);
  }

  @Transactional
  public void delete(Long memberId, Long recipeId) {
    MemberRecipe memberRecipe = memberRecipeRepository.findByMemberIdAndRecipeId(memberId, recipeId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 레시피가 아닙니다."));
    recipeRepository.delete(memberRecipe.getRecipe());
  }

  @Transactional
  public void toggleLike(Long memberId, Long recipeId) {
    MemberRecipe memberRecipe = memberRecipeRepository.findByMemberIdAndRecipeId(memberId, recipeId)
        .orElseThrow(() -> new EntityNotFoundException("레시피 좋아요 정보가 없습니다."));
    memberRecipe.toggleLike();
  }
}
