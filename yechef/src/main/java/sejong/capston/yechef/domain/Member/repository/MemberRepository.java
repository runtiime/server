package sejong.capston.yechef.domain.Member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sejong.capston.yechef.domain.Member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findById(Long Id);

  Member findByOauthId(Long oauthId);
}
