package xuandong.ecommerce_ver_2.service.user;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import xuandong.ecommerce_ver_2.entity.User;
import xuandong.ecommerce_ver_2.entity.UserRole;
import xuandong.ecommerce_ver_2.repository.UserRespository;
import xuandong.ecommerce_ver_2.repository.UserRoleRepository;

@Service
public class UserDetailCustomer implements UserDetailsService {
	private UserRespository userRespository;
	private UserRoleRepository userRoleRepository;

	public UserDetailCustomer(UserRespository userRespository, UserRoleRepository userRoleRepository) {
		this.userRespository = userRespository;
		this.userRoleRepository = userRoleRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
		User user = userRespository.findByEmailOrPhone(emailOrPhone, emailOrPhone)
				.orElseThrow(() -> new UsernameNotFoundException("user not found by " + emailOrPhone));

		// Fetch roles for the user
		List<UserRole> userRoles = userRoleRepository.findByUser(user);
		Collection<GrantedAuthority> authorities = userRoles.stream()
				.map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName())).collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}

}
