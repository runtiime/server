package sejong.capston.yechef.domain.KakaoImage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class KakaoImageResponseDto {

  private List<Document> documents;

  @Getter
  public static class Document {
    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @JsonProperty("display_sitename")
    private String siteName;

    @JsonProperty("doc_url")
    private String docUrl;

    private int width;
    private int height;
  }
}
