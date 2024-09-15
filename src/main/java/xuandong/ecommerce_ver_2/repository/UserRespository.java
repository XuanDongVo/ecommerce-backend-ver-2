package xuandong.ecommerce_ver_2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import xuandong.ecommerce_ver_2.entity.User;

public interface UserRespository extends JpaRepository<User, Long> {
		Optional<User> findByEmail(String email); 
		
		Optional<User> findByEmailOrPhone(String email , String phone);

		Optional<User> findByEmailOrPhoneAndRefreshToken(String email, String phone, String refreshToken);
 }


