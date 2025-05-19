package sejong.capston.yechef.domain.Recipe.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStepDto {
    private int stepNumber;        // 1,2,3,…
    private String action;      // ex) “떡 자르기”
    private String description; // “떡 300g을 먹기 좋은 크기로 썬다”
}
