package sejong.capston.yechef.domain.KakaoImage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import sejong.capston.yechef.domain.KakaoImage.dto.KakaoImageResponseDto;
import sejong.capston.yechef.global.exception.BaseException;
import sejong.capston.yechef.global.exception.error.ErrorCode;

@Service
@RequiredArgsConstructor
public class KakaoImageService {

  private final WebClient kakaoImageWebClient;

  public KakaoImageResponseDto searchImage(String query) {
    try {
      return kakaoImageWebClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/v2/search/image")
              .queryParam("query", query)
              .queryParam("sort", "accuracy")
              .queryParam("page", 1)
              .queryParam("size", 3) // 여러 개 받고 싶으면 size 늘리기
              .build())
          .retrieve()
          .bodyToMono(KakaoImageResponseDto.class)
          .block();
    }catch (WebClientResponseException e) {
      throw BaseException.from(ErrorCode.KAKAO_API_ERROR, "카카오 이미지 API 응답 오류: " + e.getStatusCode());
    } catch (Exception e) {
      throw BaseException.from(ErrorCode.KAKAO_API_ERROR, "카카오 이미지 API 호출 실패: " + e.getMessage());
    }
  }
}