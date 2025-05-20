package sejong.capston.yechef.domain.Image;

import jakarta.persistence.*;
import lombok.*;
import sejong.capston.yechef.domain.Recipe.Recipe;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String s3Url;
  private String s3Key; // ← 키는 동적으로 외부에서 생성해서 주입해야 함

  @OneToOne(fetch = FetchType.LAZY)
  private Recipe recipe;

  public Image(String s3Url, String s3Key) {
    this.s3Url = s3Url;
    this.s3Key = s3Key;
  }
}