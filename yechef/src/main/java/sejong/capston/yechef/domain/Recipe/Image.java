package sejong.capston.yechef.domain.Recipe;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Image {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String s3Url;

  @ManyToOne(fetch = FetchType.LAZY)
  private Recipe recipe;
}