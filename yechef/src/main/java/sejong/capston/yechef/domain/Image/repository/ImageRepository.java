package sejong.capston.yechef.domain.Image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejong.capston.yechef.domain.Image.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}