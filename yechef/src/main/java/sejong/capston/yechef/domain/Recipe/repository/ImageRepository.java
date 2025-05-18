package sejong.capston.yechef.domain.Recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.capston.yechef.domain.Recipe.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}