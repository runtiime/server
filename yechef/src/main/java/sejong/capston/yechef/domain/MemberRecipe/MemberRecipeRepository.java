package sejong.capston.yechef.domain.MemberRecipe;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRecipeRepository extends JpaRepository<MemberRecipe, Long> {

  Optional<MemberRecipe> findByMemberIdAndRecipeId(Long memberId, Long recipeId);

}

