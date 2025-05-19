package sejong.capston.yechef.domain.Image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sejong.capston.yechef.domain.Image.service.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

  // test용
  private final ImageService imageService;

  @PostMapping("/generate")
  public ResponseEntity<Void> generate(@RequestParam Long recipeId) {
    imageService.generateAndSaveImageForRecipe(recipeId);
    return ResponseEntity.ok().build();
  }
}
