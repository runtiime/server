package sejong.capston.yechef.domain.Gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepDto {
    private int   order;      // 1,2,3,…
    private String action;    // ex) “떡 자르기”
    private String description; // “떡 300g을 먹기 좋은 크기로 썬다”
}
