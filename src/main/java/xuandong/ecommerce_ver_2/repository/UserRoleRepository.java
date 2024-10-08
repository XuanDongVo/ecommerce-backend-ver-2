package xuandong.ecommerce_ver_2.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.Role;
import xuandong.ecommerce_ver_2.entity.User;
import xuandong.ecommerce_ver_2.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	List<UserRole> findByUser(User user);
	
	Page<UserRole> findByRole(Role role , Pageable pageable);
}
