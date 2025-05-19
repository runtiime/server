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

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String s3Url;

  @OneToOne(fetch = FetchType.LAZY)
  private Recipe recipe;
}