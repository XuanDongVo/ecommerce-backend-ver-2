package xuandong.ecommerce_ver_2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Order;
import xuandong.ecommerce_ver_2.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	  List<OrderDetail> findByOrder(Order order);
}
