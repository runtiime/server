package sejong.capston.yechef.domain.Recipe.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;
import sejong.capston.yechef.domain.Image.Image;
import sejong.capston.yechef.domain.Image.repository.ImageRepository;
import sejong.capston.yechef.domain.Image.service.ImageService;
import sejong.capston.yechef.domain.Ingredient.service.IngredientService;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Member.repository.MemberRepository;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.MemberRecipe.repository.MemberRecipeRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.dto.SaveRecipeRequest;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
import sejong.capston.yechef.domain.RecipeSteps.repository.RecipeStepRepository;
import sejong.capston.yechef.domain.RecipeSteps.service.RecipeStepService;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;
import sejong.capston.yechef.global.s3.service.S3UploadService;

@Service
@RequiredArgsConstructor
public class RecipeService {

  private final RecipeRepository recipeRepository;
  private final MemberRepository memberRepository;
  private final MemberRecipeRepository memberRecipeRepository;
  private final ImageRepository imageRepository;

  private final ImageService imageService;
  private final S3UploadService s3UploadService;
  private final IngredientService ingredientService;
  private final RecipeStepService recipeStepService;

  @Transactional
  public RecipeDto create(Long memberId, RecipeParseResultDto recipeDto, MultipartFile sourceImageFile) {

    // 회원 확인
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

    // 업로드 및 URL 획득 (키 생성 포함)
    String s3Url = s3UploadService.uploadAndGenerateKey(sourceImageFile);
    String s3Key = s3UploadService.extractKeyFromUrl(s3Url);

    // 이미지 저장
    Image sourceImage = imageRepository.save(Image.builder()
         .s3Url(s3Url)
         .s3Key(s3Key)
         .build());

    // Recipe 저장
    Recipe recipe = Recipe.of(
            recipeDto.getTitle(),
            member.getNickname(),
            Recipe.RecipeType.PRIVATE,
            recipeDto.getServings(),
            recipeDto.getText(),
            sourceImage);
    recipeRepository.save(recipe);

    // MemberRecipe 연결
    MemberRecipe memberRecipe = MemberRecipe.of(member, recipe);

    // Member 연결
    memberRecipeRepository.save(memberRecipe);

    // Ingredient / RecipeStep 저장
    ingredientService.saveIngredients(recipeDto.getIngredients(), recipe);
    recipeStepService.saveSteps(recipeDto.getSteps(), recipe);

    // 썸네일 자동 생성
    imageService.generateAndSaveThumbnail(recipe.getId());

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
    // 1. 현재 사용자의 MemberRecipe 조회
    MemberRecipe memberRecipe = memberRecipeRepository.findByMemberIdAndRecipeId(memberId, recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.NOT_RECIPE_OWNER));

    Recipe recipe = memberRecipe.getRecipe();

    // 2. MemberRecipe 삭제
    memberRecipeRepository.delete(memberRecipe);

    // 3. 해당 recipe가 다른 멤버에게 공유되지 않았다면 실제로 삭제
    long remainingLinks = memberRecipeRepository.countByRecipeId(recipeId);
    if (remainingLinks == 0) {
      // 3-1. 썸네일 S3 삭제
      if (recipe.getThumbnailImage() != null) {
        String thumbnailKey = recipe.getThumbnailImage().getS3Key();
        if (thumbnailKey != null) {
          s3UploadService.deleteFile(thumbnailKey);
        }
      }

      // 3-2. 원본 이미지 S3 삭제
      if (recipe.getSourceImage() != null) {
        String sourceKey = recipe.getSourceImage().getS3Key();
        if (sourceKey != null) {
          s3UploadService.deleteFile(sourceKey);
        }
      }

      // 3-3. 레시피 삭제 (Image는 cascade + orphanRemoval)
      recipeRepository.delete(recipe);
    }
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

  public List<Recipe> getPublicRecipes() {
    return recipeRepository.findByRecipeType(Recipe.RecipeType.PUBLIC);
  }

  @Transactional(readOnly = true)
  public RecipeDto getRecipeFromMemberRecipe(Long memberRecipeId) {
    MemberRecipe mr = memberRecipeRepository.findById(memberRecipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_RECIPE_NOT_FOUND));

    return RecipeDto.from(mr.getRecipe());
  }

  @Transactional
  public void deleteMemberRecipe(Long memberId, Long memberRecipeId) {
    MemberRecipe memberRecipe = memberRecipeRepository.findById(memberRecipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_RECIPE_NOT_FOUND));

    // 다른 사람이 요청한 경우 차단
    if (!memberRecipe.getMember().getId().equals(memberId)) {
      throw BaseException.from(ErrorCode.NOT_RECIPE_OWNER);
    }

    Recipe recipe = memberRecipe.getRecipe();

    // 좋아요만 누른 경우 → 연결만 삭제
    if (!memberRecipe.getIsOwner()) {
      memberRecipeRepository.delete(memberRecipe);
      return;
    }

    // 소유자인 경우 → 연결 삭제 후 레시피 사용 여부 확인
    memberRecipeRepository.delete(memberRecipe);

    long count = memberRecipeRepository.countByRecipeId(recipe.getId());
    if (count == 0) {
      if (recipe.getThumbnailImage() != null) {
        s3UploadService.deleteFile(recipe.getThumbnailImage().getS3Key());
      }
      if (recipe.getSourceImage() != null) {
        s3UploadService.deleteFile(recipe.getSourceImage().getS3Key());
      }

      recipeRepository.delete(recipe);
    }
  }

}
