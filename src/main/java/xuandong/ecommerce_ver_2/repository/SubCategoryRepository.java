package xuandong.ecommerce_ver_2.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.mapping.Subclass;
import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Category;
import xuandong.ecommerce_ver_2.entity.SubCategory;


public interface SubCategoryRepository  extends JpaRepository<SubCategory, Long>{
	  List<SubCategory> findByCategory(Category category);
	  
	  Optional<SubCategory> findByName(String name);
	  
	  Optional<SubCategory>  findById(Long id);
	  
}
