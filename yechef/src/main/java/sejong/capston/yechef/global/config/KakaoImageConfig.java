package sejong.capston.yechef.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Getter
public class KakaoImageConfig {

  @Value("${KAKAO_REST_API_KEY}")
  private String apiKey;

  @Bean
  public WebClient kakaoImageWebClient() {
    return WebClient.builder()
        .baseUrl("https://dapi.kakao.com")
        .defaultHeader("Authorization", "KakaoAK " + apiKey)
        .build();
  }
}
