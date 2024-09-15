package xuandong.ecommerce_ver_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Cart;
import xuandong.ecommerce_ver_2.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUser(User user);
}
