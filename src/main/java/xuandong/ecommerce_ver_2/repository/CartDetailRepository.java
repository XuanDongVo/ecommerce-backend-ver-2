package xuandong.ecommerce_ver_2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import xuandong.ecommerce_ver_2.entity.Cart;
import xuandong.ecommerce_ver_2.entity.CartDetail;
import xuandong.ecommerce_ver_2.entity.ProductSku;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
	Optional<CartDetail> findByProductSkuAndCart(ProductSku productSku , Cart cart);
	
	List<CartDetail> findByCart(Cart cart);
	
	@Query(value = "SELECT * FROM  cart_detail where cart_detail.id in :ids" , nativeQuery = true)
	List<CartDetail> findbyIds(List<Long> ids);
}
