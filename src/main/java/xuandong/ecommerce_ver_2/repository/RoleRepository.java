package xuandong.ecommerce_ver_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(String name);
}
