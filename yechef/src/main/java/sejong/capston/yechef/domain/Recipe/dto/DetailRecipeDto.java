package sejong.capston.yechef.domain.Recipe.dto;

import lombok.Builder;
import lombok.Getter;
import sejong.capston.yechef.domain.Image.Dto.ImageDto;
import sejong.capston.yechef.domain.Image.Image;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.Recipe.Recipe;
import sejong.capston.yechef.domain.RecipeSteps.RecipeStep;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class DetailRecipeDto {
  private Long id;
  private String title;
  private int likeCount;
  private String author;
  private double ranking;
  private int servings;
  private String text;
  private Recipe.RecipeType recipeType;
  private boolean isUpdated;
  private List<RecipeStepDto> recipeSteps;

  private ImageDto thumbnailImage;
  private ImageDto sourceImage;

  public static DetailRecipeDto from(Recipe recipe) {
    return DetailRecipeDto.builder()
        .id(recipe.getId())
        .title(recipe.getTitle())
        .likeCount(recipe.getLikeCount())
        .author(recipe.getAuthor())
        .ranking(recipe.getRanking())
        .servings(recipe.getServings())
        .text(recipe.getText())
        .recipeType(recipe.getRecipeType())
        .isUpdated(recipe.isUpdated())
        .recipeSteps(recipe.getRecipeSteps() != null
            ? recipe.getRecipeSteps().stream()
            .map(RecipeStepDto::from)
            .collect(Collectors.toList())
            : null)
        .thumbnailImage(recipe.getThumbnailImage() != null
            ? ImageDto.from(recipe.getThumbnailImage())
            : null)
        .sourceImage(recipe.getSourceImage() != null
            ? ImageDto.from(recipe.getSourceImage())
            : null)
        .build();
  }



}
