package sejong.capston.yechef.domain.Recipe.service;


import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;
import sejong.capston.yechef.domain.Gpt.service.GptService;
import sejong.capston.yechef.domain.Image.Image;
import sejong.capston.yechef.domain.Image.repository.ImageRepository;
import sejong.capston.yechef.domain.Image.service.ImageService;
import sejong.capston.yechef.domain.Ingredient.service.IngredientService;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Member.repository.MemberRepository;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.MemberRecipe.repository.MemberRecipeRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.ai.OcrClient;
import sejong.capston.yechef.domain.Recipe.dto.DetailRecipeDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
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
  private final GptService gptService;
  private final S3UploadService s3UploadService;
  private final IngredientService ingredientService;
  private final RecipeStepService recipeStepService;
  private final OcrClient ocrClient;

  @Transactional
  public RecipeDto create(Long memberId, RecipeParseResultDto recipeDto) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

    String s3Url = recipeDto.getSourceImageUrl();
    String s3Key = s3UploadService.extractKeyFromUrl(s3Url);

    Image sourceImage = imageRepository.save(Image.builder()
        .s3Url(s3Url)
        .s3Key(s3Key)
        .build());

    Recipe recipe = Recipe.of(
        recipeDto.getTitle(),
        member.getNickname(),
        Recipe.RecipeType.PRIVATE,
        recipeDto.getServings(),
        recipeDto.getText(),
        sourceImage);
    recipeRepository.save(recipe);

    memberRecipeRepository.save(MemberRecipe.of(member, recipe));
    ingredientService.saveIngredients(recipeDto.getIngredients(), recipe);
    recipeStepService.saveSteps(recipeDto.getSteps(), recipe);
    imageService.generateAndSaveThumbnail(recipe.getId());

    return RecipeDto.from(recipe);
  }


  @Transactional(readOnly = true)
  public List<RecipeDto> getMyRecipes(Long memberId) {
    List<Recipe> recipes = recipeRepository.findByMemberId(memberId);
    return recipes.stream().map(RecipeDto::from).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public DetailRecipeDto getRecipe(Long recipeId) {
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));
    return DetailRecipeDto.from(recipe);
  }

  @Transactional
  public void delete(Long memberId, Long recipeId) {
    MemberRecipe memberRecipe = memberRecipeRepository.findByMemberIdAndRecipeId(memberId, recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.NOT_RECIPE_OWNER));

    Recipe recipe = memberRecipe.getRecipe();
    memberRecipeRepository.delete(memberRecipe);

    long remainingLinks = memberRecipeRepository.countByRecipeId(recipeId);
    if (remainingLinks == 0) {
      if (recipe.getThumbnailImage() != null) {
        s3UploadService.deleteFile(recipe.getThumbnailImage().getS3Key());
      }
      if (recipe.getSourceImage() != null) {
        s3UploadService.deleteFile(recipe.getSourceImage().getS3Key());
      }
      recipeRepository.delete(recipe);
    }
  }

  @Transactional
  public void toggleLike(Long memberId, Long recipeId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

    MemberRecipe memberRecipe = memberRecipeRepository.findByMemberIdAndRecipeId(memberId, recipeId).orElse(null);
    if (memberRecipe == null) {
      memberRecipe = MemberRecipe.builder()
          .member(member)
          .recipe(recipe)
          .isOwner(false)
          .isLiked(true)
          .build();
      memberRecipeRepository.save(memberRecipe);
      recipe.setLikeCount(recipe.getLikeCount() + 1);
    } else {
      boolean wasLiked = memberRecipe.getIsLiked();
      memberRecipe.toggleLike();
      recipe.setLikeCount(recipe.getLikeCount() + (memberRecipe.getIsLiked() ? 1 : -1));
    }
  }

  @Transactional(readOnly = true)
  public List<RecipeDto> getLikedRecipes(Long memberId) {
    List<Recipe> likedRecipes = memberRecipeRepository.findLikedRecipesByMemberId(memberId);
    return likedRecipes.stream().map(RecipeDto::from).collect(Collectors.toList());
  }

  public List<RecipeDto> getPublicRecipes() {
    return recipeRepository
        .findByRecipeType(Recipe.RecipeType.PUBLIC)
        .stream()
        .map(RecipeDto::from)
        .collect(Collectors.toList());
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

    if (!memberRecipe.getMember().getId().equals(memberId)) {
      throw BaseException.from(ErrorCode.NOT_RECIPE_OWNER);
    }

    Recipe recipe = memberRecipe.getRecipe();
    if (!memberRecipe.getIsOwner()) {
      memberRecipeRepository.delete(memberRecipe);
      return;
    }

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

  @Transactional
  public RecipeDto createRecipeFromImage(Long memberId, MultipartFile imageFile) {
    // 1. 이미지 저장
    Image image = imageService.saveImage(imageFile);

    // 2. OCR: raw text 추출
    String rawText = ocrClient.extractText(imageFile);

    // 3. GPT 파싱 요청
    RecipeParseResultDto dto = gptService.parseRecipe(rawText);
    dto.setSourceImageUrl(image.getS3Url());

    // 4. 레시피 생성
    Recipe recipe = createFromOcr(memberId, dto, image);
    return RecipeDto.from(recipe);
  }


  public Recipe createFromOcr(Long memberId, RecipeParseResultDto dto, Image image) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

    Recipe recipe = Recipe.of(
        dto.getTitle(),
        member.getNickname(),
        Recipe.RecipeType.PRIVATE,
        dto.getServings(),
        dto.getText(),
        image // 저장된 이미지 그대로 사용
    );

    recipeRepository.save(recipe);
    memberRecipeRepository.save(MemberRecipe.of(member, recipe));
    ingredientService.saveIngredients(dto.getIngredients(), recipe);
    recipeStepService.saveSteps(dto.getSteps(), recipe);
    imageService.generateAndSaveThumbnail(recipe.getId());

    return recipe;
  }

}