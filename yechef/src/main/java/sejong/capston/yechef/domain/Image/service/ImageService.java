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
  public void generateAndSaveThumbnail(Long recipeId) {
    Recipe recipe = recipeRepository.findById(recipeId)
        .orElseThrow(() -> BaseException.from(ErrorCode.RECIPE_NOT_EXIST));

    String imageUrl = kakaoImageService.getTopImageUrl(recipe.getTitle());
    String s3Key = extractKeyFromUrl(imageUrl); // 외부 URL이면 null

    Image thumbnailImage = Image.builder()
        .s3Url(imageUrl)
        .s3Key(s3Key)
        .recipe(recipe)
        .build();

    imageRepository.save(thumbnailImage);
    recipe.setThumbnailImage(thumbnailImage);
  }


  private String extractKeyFromUrl(String url) {
    // Kakao URL이면 key가 없는 외부 이미지일 수도 있음
    if (url == null || !url.contains(".amazonaws.com/")) return null;

    // ex: https://bucket-name.s3.region.amazonaws.com/source-images/abc.jpg
    return url.substring(url.indexOf(".com/") + 5);
  }

}
