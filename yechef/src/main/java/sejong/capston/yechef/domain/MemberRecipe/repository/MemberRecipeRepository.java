package sejong.capston.yechef.domain.MemberRecipe.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;

public interface MemberRecipeRepository extends JpaRepository<MemberRecipe, Long> {

  Optional<MemberRecipe> findByMemberIdAndRecipeId(Long memberId, Long recipeId);

}

