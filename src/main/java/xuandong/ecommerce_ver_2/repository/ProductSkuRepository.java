package xuandong.ecommerce_ver_2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import xuandong.ecommerce_ver_2.entity.ProductColorImg;
import xuandong.ecommerce_ver_2.entity.ProductSku;
import xuandong.ecommerce_ver_2.entity.Size;

public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> , JpaSpecificationExecutor<ProductSku> {

	@Query(value = "SELECT pu.* " + "FROM product_sku pu "
			+ "INNER JOIN product_color_img pci ON pu.product_color_img_id = pci.id "
			+ "INNER JOIN product p ON pci.product_id = p.id " + "WHERE p.id = :id", nativeQuery = true)
	List<ProductSku> findByProductId(@Param("id") Long id);
	
	@Query(value = "SELECT pu.* " + "FROM product_sku pu "
			+ "INNER JOIN product_color_img pci ON pu.product_color_img_id = pci.id "
			+ "INNER JOIN product p ON pci.product_id = p.id " + "WHERE p.id  IN :productIds" + " AND p.is_delete = 0 " + "AND pci.is_delete= 0", nativeQuery = true)
	List<ProductSku> findByProductIds(@Param("productIds") List<Long> productIds);
	
	Optional<ProductSku> findByProductColorImgAndSize(ProductColorImg productColorImg ,Size size);

}
