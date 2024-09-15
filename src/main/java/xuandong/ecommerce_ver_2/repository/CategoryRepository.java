package xuandong.ecommerce_ver_2.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import xuandong.ecommerce_ver_2.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category>  findByName(String name);
}
