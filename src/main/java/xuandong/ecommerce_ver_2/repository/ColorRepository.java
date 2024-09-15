package xuandong.ecommerce_ver_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Color;

public interface ColorRepository extends JpaRepository<Color, Long> {
	Optional<Color> findByName(String name);
}
