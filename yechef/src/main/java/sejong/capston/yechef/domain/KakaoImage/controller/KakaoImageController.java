package sejong.capston.yechef.domain.KakaoImage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sejong.capston.yechef.domain.KakaoImage.dto.KakaoImageResponseDto;
import sejong.capston.yechef.domain.KakaoImage.service.KakaoImageService;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class KakaoImageController {

  private final KakaoImageService kakaoImageService;

  @GetMapping
  public ResponseEntity<KakaoImageResponseDto> search(@RequestParam String query) {
    KakaoImageResponseDto result = kakaoImageService.searchImage(query);
    return ResponseEntity.ok(result);
  }
}