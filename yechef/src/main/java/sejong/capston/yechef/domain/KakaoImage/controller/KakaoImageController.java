package sejong.capston.yechef.domain.KakaoImage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sejong.capston.yechef.domain.KakaoImage.dto.KakaoImageResponseDto;
import sejong.capston.yechef.domain.KakaoImage.service.KakaoImageService;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class KakaoImageController {

  private final KakaoImageService kakaoImageService;

  /**
   * 전체 이미지 목록 반환 (원본 그대로 전달)
   */
  @GetMapping
  public ResponseEntity<KakaoImageResponseDto> search(@RequestParam String query) {
    KakaoImageResponseDto result = kakaoImageService.searchImage(query);
    return ResponseEntity.ok(result);
  }

  /**
   * 필터링된 대표 이미지 1개만 반환
   */
  @GetMapping("/top")
  public ResponseEntity<String> getTopImage(@RequestParam String query) {
    String imageUrl = kakaoImageService.getTopImageUrl(query);
    return ResponseEntity.ok(imageUrl);
  }
}