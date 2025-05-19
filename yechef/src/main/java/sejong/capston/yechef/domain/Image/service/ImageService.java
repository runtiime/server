package sejong.capston.yechef.domain.Image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejong.capston.yechef.domain.Image.Image;
import sejong.capston.yechef.domain.Image.repository.ImageRepository;
import sejong.capston.yechef.domain.KakaoImage.service.KakaoImageService;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.Recipe.repository.RecipeRepository;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository imageRepository;
  private final RecipeRepository recipeRepository;
  private final KakaoImageService kakaoImageService;

  @Transactional
  public void generateAndSaveImageForRecipe(Long recipeId) {
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST, "해당 레시피가 존재하지 않습니다."));

    String imageUrl = kakaoImageService.getTopImageUrl(recipe.getTitle());

    Image image = Image.builder()
        .s3Url(imageUrl)
        .recipe(recipe)
        .build();

    imageRepository.save(image);

    recipe.setImage(image);
  }
}
