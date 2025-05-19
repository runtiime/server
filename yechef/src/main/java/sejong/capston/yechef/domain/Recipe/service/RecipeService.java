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
import sejong.capston.yechef.domain.MemberRecipe.repository.MemberRecipeRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeCreateDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

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

    Recipe recipe = Recipe.of(dto.getTitle(),
            member.getNickname(),
            dto.getRecipeType());
    recipeRepository.save(recipe);

    MemberRecipe memberRecipe = MemberRecipe.builder()
        .member(member)
        .recipe(recipe)
        .isOwner(true)
        .isLiked(false)
        .build();
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
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));
    return RecipeDto.from(recipe);
  }

  @Transactional
  public void delete(Long memberId, Long recipeId) {
    MemberRecipe memberRecipe = memberRecipeRepository.findByMemberIdAndRecipeId(memberId, recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.NOT_RECIPE_OWNER));
    recipeRepository.delete(memberRecipe.getRecipe());
  }

  @Transactional
  public void toggleLike(Long memberId, Long recipeId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

    MemberRecipe memberRecipe = memberRecipeRepository.findByMemberIdAndRecipeId(memberId, recipeId)
        .orElse(null);

    if (memberRecipe == null) {
      // 처음 좋아요 누름 → 새로 생성
      memberRecipe = MemberRecipe.builder()
          .member(member)
          .recipe(recipe)
          .isOwner(false)
          .isLiked(true)
          .build();
      memberRecipeRepository.save(memberRecipe);
      recipe.setLikeCount(recipe.getLikeCount() + 1);
    } else {
      // 기존 존재 → 좋아요 토글
      boolean wasLiked = memberRecipe.getIsLiked();
      memberRecipe.toggleLike();
      recipe.setLikeCount(recipe.getLikeCount() + (memberRecipe.getIsLiked() ? 1 : -1));
    }
  }
  @Transactional(readOnly = true)
  public List<RecipeDto> getLikedRecipes(Long memberId) {
    List<Recipe> likedRecipes = memberRecipeRepository.findLikedRecipesByMemberId(memberId);
    return likedRecipes.stream()
        .map(RecipeDto::from)
        .collect(Collectors.toList());
  }


}
