package xuandong.ecommerce_ver_2.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import xuandong.ecommerce_ver_2.entity.Product;
import xuandong.ecommerce_ver_2.entity.SubCategory;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	Page<Product> findBySubCategory(SubCategory subCategory, Pageable pageble);
	
	Optional<Product> findByName(String name);

	@Query(value = "SELECT p FROM Product p "
			+ "INNER JOIN p.subCategory sub "
			+ "INNER JOIN sub.category c "
			+ "INNER JOIN c.gender g "
			+ "WHERE g.name = :gender",
			countQuery = "SELECT COUNT(p) FROM Product p "
					+ "INNER JOIN p.subCategory sub "
					+ "INNER JOIN sub.category c "
					+ "INNER JOIN c.gender g "
					+ "WHERE g.name = :gender")
	Page<Product> findByGender( @Param("gender") String gender, Pageable pageble);
	
	@Query(value = "SELECT p FROM Product p "
			+ "INNER JOIN p.subCategory sub "
			+ "INNER JOIN sub.category c "
			+ "WHERE c.name = :category",
			countQuery = "SELECT COUNT(p) FROM Product p "
					+ "INNER JOIN p.subCategory sub "
					+ "INNER JOIN sub.category c "
					+ "WHERE c.name = :category")
	Page<Product> findByCategory( @Param("category") String category, Pageable pageble);

}
