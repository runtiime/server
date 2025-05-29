package sejong.capston.yechef.domain.Recipe.service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sejong.capston.yechef.domain.Gpt.dto.IngredientDto;
import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;
import sejong.capston.yechef.domain.Gpt.service.GptService;
import sejong.capston.yechef.domain.Image.Dto.ImageDto;
import sejong.capston.yechef.domain.Image.Image;
import sejong.capston.yechef.domain.Image.repository.ImageRepository;
import sejong.capston.yechef.domain.Image.service.ImageService;
import sejong.capston.yechef.domain.Ingredient.Ingredient;
import sejong.capston.yechef.domain.Ingredient.service.IngredientService;
import sejong.capston.yechef.domain.Member.Member;
import sejong.capston.yechef.domain.Member.repository.MemberRepository;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.MemberRecipe.repository.MemberRecipeRepository;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.ai.OcrClient;
import sejong.capston.yechef.domain.Recipe.dto.DetailRecipeDto;
import sejong.capston.yechef.domain.Recipe.dto.OcrRecipeResultDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeIngredientDto;
import sejong.capston.yechef.domain.Recipe.dto.RecipeStepDto;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;
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
  public OcrRecipeResultDto createRecipeFromImage(Long memberId, MultipartFile imageFile) {
    // 1. 이미지 저장
    Image image = imageService.saveImage(imageFile);

    // 2. OCR: raw text 추출
    String rawText = ocrClient.extractText(imageFile);

    // 3. GPT 파싱 요청
    RecipeParseResultDto dto = gptService.parseRecipe(rawText);
    dto.setSourceImageUrl(image.getS3Url());

    // 4. 레시피 생성
    Recipe recipe = createFromOcr(memberId, dto, image);
    return OcrRecipeResultDto.from(recipe, rawText);
  }


  @Transactional
  public Recipe createFromOcr(Long memberId, RecipeParseResultDto dto, Image image) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

    Recipe recipe = Recipe.of(
        dto.getTitle(),
        member.getNickname(),
        Recipe.RecipeType.PRIVATE,
        dto.getServings(),
        dto.getText(),
        image
    );

    dto.getIngredients().forEach(ingDto -> {
      Ingredient ing = Ingredient.of(
          ingDto.getName(),
          ingDto.getQuantity(),
          recipe
      );
      recipe.addIngredient(ing);
    });

    dto.getSteps().forEach(stepDto -> {
      RecipeStep step = RecipeStep.of(
          stepDto.getStepNumber(),
          stepDto.getAction(),
          stepDto.getIngredients(),
          stepDto.getDescription(),
          recipe
      );
      recipe.addStep(step);
    });

    recipeRepository.save(recipe);
    memberRecipeRepository.save(MemberRecipe.of(member, recipe));
    imageService.generateAndSaveThumbnail(recipe.getId());

    return recipe;
  }

  public DetailRecipeDto getScaledRecipe(Long recipeId, int targetServings) {
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

    double ratio = (double) targetServings / recipe.getServings();
    if (targetServings < 1 || targetServings > 10) {
      throw BaseException.from(ErrorCode.RECIPE_INVALID_SERVINGS);
    }

    List<RecipeIngredientDto> scaledIngs = recipe.getIngredients().stream()
        .map(ing -> {
          String scaled = scaleAmount(ing.getOriginalAmount(), ratio);
          return RecipeIngredientDto.of(
              ing.getOriginalName(),
              ing.getOriginalAmount(),
              scaled
          );
        })
        .collect(Collectors.toList());

    List<RecipeStepDto> steps = recipe.getRecipeSteps().stream()
        .map(step -> {
          String scaledDesc = scaleAllNumbers(step.getDescription(), ratio);
          return RecipeStepDto.builder()
              .stepNumber(step.getStepNumber())
              .action(step.getAction())
              .ingredients(step.getIngredients())
              .description(scaledDesc)
              .build();
        })
        .collect(Collectors.toList());

    return DetailRecipeDto.builder()
        .id(recipe.getId())
        .title(recipe.getTitle())
        .likeCount(recipe.getLikeCount())
        .author(recipe.getAuthor())
        .ranking(recipe.getRanking())
        .servings(targetServings)
        .text(recipe.getText())
        .recipeType(recipe.getRecipeType())
        .isUpdated(recipe.isUpdated())
        .ingredients(scaledIngs)
        .recipeSteps(steps)
        .thumbnailImage(ImageDto.from(recipe.getThumbnailImage()))
        .sourceImage(ImageDto.from(recipe.getSourceImage()))
        .build();
  }

  private String scaleAmount(String original, double ratio) {
    Matcher m = Pattern.compile("^(\\d+(?:\\.\\d+)?)(.*)$").matcher(original);
    if (!m.matches()) {
      return original;
    }
    BigDecimal value = new BigDecimal(m.group(1))
        .multiply(BigDecimal.valueOf(ratio))
        .setScale(2, RoundingMode.HALF_UP);
    String num = value.stripTrailingZeros().toPlainString();
    return num + m.group(2);
  }

  private String scaleAllNumbers(String text, double ratio) {
    Pattern numberPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    Matcher m = numberPattern.matcher(text);
    StringBuffer sb = new StringBuffer();

    while (m.find()) {
      String numStr = m.group(1);
      int posAfterNum = m.end(1);

      // 숫자 다음에 오는 공백을 모두 건너뛴 뒤 실제 문자를 검사
      int idx = posAfterNum;
      while (idx < text.length() && Character.isWhitespace(text.charAt(idx))) {
        idx++;
      }

      boolean isTimeUnit = false;
      if (idx < text.length()) {
        char c = text.charAt(idx);
        if (c == '분' || c == '초') {
          isTimeUnit = true;
        } else if (text.startsWith("시간", idx)) {
          isTimeUnit = true;
        }
      }

      String replacement;
      if (isTimeUnit) {
        // 시간 단위 숫자: 변경 없이 그대로
        replacement = numStr;
      } else {
        // 재료량 등은 스케일 적용
        BigDecimal scaled = new BigDecimal(numStr)
            .multiply(BigDecimal.valueOf(ratio))
            .setScale(2, RoundingMode.HALF_UP);
        replacement = scaled.stripTrailingZeros().toPlainString();
      }

      m.appendReplacement(sb, replacement);
    }
    m.appendTail(sb);
    return sb.toString();
  }


}