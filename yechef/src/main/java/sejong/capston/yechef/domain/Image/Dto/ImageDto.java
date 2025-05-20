package sejong.capston.yechef.domain.Image.Dto;

import lombok.Builder;
import lombok.Getter;
import sejong.capston.yechef.domain.Image.Image;

@Getter
@Builder
public class ImageDto {
  private Long id;
  private String s3Url;
  private String s3Key;

  public static ImageDto from(Image image) {
    if (image == null) return null;

    return ImageDto.builder()
        .id(image.getId())
        .s3Url(image.getS3Url())
        .s3Key(image.getS3Key())
        .build();
  }
}