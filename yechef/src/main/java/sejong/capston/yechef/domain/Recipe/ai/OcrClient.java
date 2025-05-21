package sejong.capston.yechef.domain.Recipe.ai;

import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import sejong.capston.yechef.domain.Gpt.dto.RecipeParseResultDto;

@Component
public class OcrClient {

  @Qualifier("aiWebClient")
  private final WebClient webClient;

  public OcrClient(@Qualifier("aiWebClient") WebClient webClient) {
    this.webClient = webClient;
  }


  public RecipeParseResultDto extractText(String imageUrl) {
    return webClient.post()
        .uri("/ocr/parse")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(Map.of("imageUrl", imageUrl))
        .retrieve()
        .bodyToMono(RecipeParseResultDto.class)
        .block();
  }

}