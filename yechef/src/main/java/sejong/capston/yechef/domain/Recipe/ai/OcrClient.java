package sejong.capston.yechef.domain.Recipe.ai;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;

@Component
public class OcrClient {

  @Qualifier("aiWebClient")
  private final WebClient webClient;

  public OcrClient(@Qualifier("aiWebClient") WebClient webClient) {
    this.webClient = webClient;
  }


//  public String extractText(MultipartFile imageFile) {
//    MultipartBodyBuilder builder = new MultipartBodyBuilder();
//    builder.part("image", imageFile.getResource());
//
//    return webClient.post()
//        .uri("/api/ocr")
//        .contentType(MediaType.MULTIPART_FORM_DATA)
//        .bodyValue(builder.build())
//        .retrieve()
//        .bodyToMono(String.class)
//        .block();   // raw OCR text
//  }

  public String extractText(MultipartFile imageFile) {
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    try {
      builder.part("image", new ByteArrayResource(imageFile.getBytes()) {
        @Override
        public String getFilename() {
          return imageFile.getOriginalFilename();
        }
      });
    } catch (IOException e) {
      throw new RuntimeException("Failed to read image file", e);
    }

    return webClient.post()
        .uri("/api/ocr")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData(builder.build()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Map<String, List<String>>>() {})
        .map(map -> String.join("\n", map.getOrDefault("texts", List.of())))
        .block();
  }

}