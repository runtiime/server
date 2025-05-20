package sejong.capston.yechef.domain.MemberRecipe.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sejong.capston.yechef.domain.MemberRecipe.MemberRecipe;
import sejong.capston.yechef.domain.Recipe.Recipe;

public interface MemberRecipeRepository extends JpaRepository<MemberRecipe, Long> {

  Optional<MemberRecipe> findByMemberIdAndRecipeId(Long memberId, Long recipeId);

  @Query("SELECT mr.recipe FROM MemberRecipe mr WHERE mr.member.id = :memberId AND mr.isLiked = true")
  List<Recipe> findLikedRecipesByMemberId(@Param("memberId") Long memberId);

}

