package xuandong.ecommerce_ver_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Color;
import xuandong.ecommerce_ver_2.entity.Product;
import xuandong.ecommerce_ver_2.entity.ProductColorImg;

public interface ProductColorImgRepository extends JpaRepository<ProductColorImg, Long> {
	Optional<ProductColorImg> findByProductAndColor(Product product, Color color);
	
}
