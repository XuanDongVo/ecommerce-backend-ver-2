package xuandong.ecommerce_ver_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
