package xuandong.ecommerce_ver_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Inventory;
import xuandong.ecommerce_ver_2.entity.ProductSku;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
	Optional<Inventory> findByProductSkus(ProductSku productSkus);
}
