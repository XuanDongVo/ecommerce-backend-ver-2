package xuandong.ecommerce_ver_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Size;

public interface SizeRepository extends JpaRepository<Size, Long> {
	Optional<Size> findByName(String name);
}
