package sejong.capston.yechef.domain.Recipe.ai;

import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;

@Component
public class OcrClient {

  @Qualifier("aiWebClient")
  private final WebClient webClient;

  public OcrClient(@Qualifier("aiWebClient") WebClient webClient) {
    this.webClient = webClient;
  }


  public String extractText(MultipartFile imageFile) {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("file", imageFile.getResource());

    return webClient.post()
        .uri("/api/ocr")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(builder.build())
        .retrieve()
        .bodyToMono(String.class)
        .block();   // raw OCR text
  }

}