package xuandong.ecommerce_ver_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Gender;
import java.util.Optional;


public interface GenderRepository  extends JpaRepository<Gender, Long>{
	
	Optional<Gender> findByName(String name);

}
