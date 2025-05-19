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

  /**
   * 카카오 이미지 검색 API 호출
   */
  public KakaoImageResponseDto searchImage(String query) {
    try {
      return kakaoImageWebClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/v2/search/image")
              .queryParam("query", query)
              .queryParam("sort", "accuracy")
              .queryParam("page", 1)
              .queryParam("size", 10) // 여러 개 받아서 필터링
              .build())
          .retrieve()
          .bodyToMono(KakaoImageResponseDto.class)
          .block();
    } catch (WebClientResponseException e) {
      throw BaseException.from(ErrorCode.KAKAO_API_ERROR, "카카오 이미지 API 응답 오류: " + e.getStatusCode());
    } catch (Exception e) {
      throw BaseException.from(ErrorCode.KAKAO_API_ERROR, "카카오 이미지 API 호출 실패: " + e.getMessage());
    }
  }

  /**
   * 검색 키워드를 보강하고, 필터링된 이미지 중 가장 상단 URL 반환
   */
  public String getTopImageUrl(String keyword) {
    String enhancedQuery = keyword; // 키워드 보강 가능

    KakaoImageResponseDto response = searchImage(enhancedQuery);

    return response.getDocuments().stream()
        .filter(doc -> isValidImage(doc.getSiteName(), doc.getImageUrl()))
        .findFirst()
        .map(KakaoImageResponseDto.Document::getImageUrl)
        .orElseThrow(() -> BaseException.from(ErrorCode.KAKAO_API_ERROR, "유효한 음식 이미지가 없습니다."));
  }

  /**
   * 유효 이미지 필터링 기준:
   * - 쇼핑몰/블로그/스마트스토어 등 상업적 이미지 제외
   * - 썸네일 또는 텍스트 이미지 URL 제거
   */
  private boolean isValidImage(String siteName, String imageUrl) {
    if (siteName == null || imageUrl == null) return false;

    String lowerSite = siteName.toLowerCase();
    String lowerUrl = imageUrl.toLowerCase();

    return !lowerSite.contains("blog")
        && !lowerSite.contains("쇼핑")
        && !lowerUrl.contains("smartstore")
        && !lowerUrl.contains("shopping")
        && !lowerUrl.contains("thumbnail")
        && !lowerUrl.endsWith(".svg"); // 아이콘, 벡터 이미지 제거
  }
}
