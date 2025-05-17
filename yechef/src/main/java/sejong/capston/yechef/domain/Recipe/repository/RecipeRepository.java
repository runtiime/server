package sejong.capston.yechef.domain.Recipe.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sejong.capston.yechef.domain.Recipe.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

  @Query("SELECT r FROM Recipe r JOIN r.memberRecipes mr WHERE mr.member.id = :memberId")
  List<Recipe> findByMemberId(@Param("memberId") Long memberId);

}