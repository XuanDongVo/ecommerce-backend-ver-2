package xuandong.ecommerce_ver_2.service.user;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import xuandong.ecommerce_ver_2.dto.request.LoginUser;
import xuandong.ecommerce_ver_2.dto.request.RegisterUserDto;
import xuandong.ecommerce_ver_2.dto.request.UpdateUserRequest;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.dto.response.UserResponse;
import xuandong.ecommerce_ver_2.dto.response.admin.InformationAccountResponse;
import xuandong.ecommerce_ver_2.entity.Cart;
import xuandong.ecommerce_ver_2.entity.Role;
import xuandong.ecommerce_ver_2.entity.User;
import xuandong.ecommerce_ver_2.entity.UserRole;
import xuandong.ecommerce_ver_2.exception.IdException;
import xuandong.ecommerce_ver_2.exception.UserAlreadyExistsException;
import xuandong.ecommerce_ver_2.repository.CartRepository;
import xuandong.ecommerce_ver_2.repository.RoleRepository;
import xuandong.ecommerce_ver_2.repository.UserRespository;
import xuandong.ecommerce_ver_2.repository.UserRoleRepository;
import xuandong.ecommerce_ver_2.service.jwt.JwtService;

@Service
public class UserService {
	private PasswordEncoder passwordEncoder;
	private UserRespository userRespository;
	private RoleRepository roleRepository;
	private UserRoleRepository userRoleRepository;
	private CartRepository cartRepository;
	private AuthenticationManager authenticationManager;
	private JwtService jwtService;

	public UserService(UserRespository userRespository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
			UserRoleRepository userRoleRepository, CartRepository cartRepository,
			AuthenticationManager authenticationManager, JwtService jwtService) {
		this.userRespository = userRespository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.userRoleRepository = userRoleRepository;
		this.cartRepository = cartRepository;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	// register user
	@Transactional
	public void registerUser(RegisterUserDto userDto) {
		// Kiểm tra xem người dùng đã tồn tại hay chưa
		Optional<User> currentUser = userRespository.findByEmailOrPhone(userDto.getEmail(), userDto.getPhone());

		if (currentUser.isPresent()) {
			// Ném ngoại lệ nếu người dùng đã tồn tại
			throw new UserAlreadyExistsException("Người dùng với email hoặc số điện thoại này đã tồn tại.");
		}

		// Tạo người dùng mới
		User user = createUser(userDto);
		userRespository.save(user);

		// Gán vai trò cho người dùng
		assignRoleToUser(user, "ROLE_USER");

		// Tạo giỏ hàng cho người dùng
		createCartForUser(user);
	}

	// register staff
	@Transactional
	public void registerStaff(RegisterUserDto userDto) {
		// Kiểm tra xem người dùng đã tồn tại hay chưa
		Optional<User> currentUser = userRespository.findByEmailOrPhone(userDto.getEmail(), userDto.getPhone());

		if (currentUser.isPresent()) {
			// Ném ngoại lệ nếu người dùng đã tồn tại
			throw new UserAlreadyExistsException("Người dùng với email hoặc số điện thoại này đã tồn tại.");
		}

		// Tạo người dùng mới
		User user = createUser(userDto);
		userRespository.save(user);

		// Gán vai trò cho người dùng
		assignRoleToUser(user, "ROLE_STAFF");

		// Tạo giỏ hàng cho người dùng
		createCartForUser(user);
	}

	// login
	public RestResponse<UserResponse> loginUser(LoginUser loginUser, HttpServletResponse response) {
		// Authenticate the user
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginUser.getEmailOrPhone(), loginUser.getPassword()));

		User user = userRespository.findByEmailOrPhone(loginUser.getEmailOrPhone(), loginUser.getEmailOrPhone()).get();

		// create accessToken
		String accessToken = jwtService.createAccessToken(authentication);

		UserResponse loginResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(),
				user.getAddress(), accessToken);

		// create refeshToken
		String refreshToken = jwtService.createRefreshToken(authentication);
		user.setRefreshToken(refreshToken);
		userRespository.save(user);

		ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken).httpOnly(true).secure(true).path("/")
				.maxAge(jwtService.getRefreshTokenExpiration()).build();
		response.addHeader("Set-Cookie", cookie.toString());

		// Create a response object
		RestResponse<UserResponse> restResponse = new RestResponse<>();
		restResponse.setStatusCode(HttpStatus.OK.value());
		restResponse.setMessage("Login successful");
		restResponse.setData(loginResponse); // Set any additional data if required

		// Return the response
		return restResponse;
	}

	// Logout
	public void logout(HttpServletResponse response) {
		// Tạo một cookie rỗng với cùng tên và các thuộc tính như refresh_token
		ResponseCookie cookie = ResponseCookie.from("refresh_token", "").httpOnly(true).secure(true).path("/").maxAge(0)
				.build();

		// Thêm cookie rỗng vào phản hồi để xóa cookie từ trình duyệt
		response.addHeader("Set-Cookie", cookie.toString());
	}

	@Transactional
	public void updateAccount(UpdateUserRequest updateUserRequest) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRespository.findByEmailOrPhone(authentication.getName(), authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		if (updateUserRequest.getEmail() != null) {
			user.setEmail(updateUserRequest.getEmail());
		}

		if (updateUserRequest.getPhone() != null) {
			user.setPhone(updateUserRequest.getPhone());
		}

		if (updateUserRequest.getName() != null) {
			user.setName(updateUserRequest.getName());
		}

		if (updateUserRequest.getAddress() != null) {
			user.setAddress(updateUserRequest.getAddress());
		}

		// Cập nhật thời gian chỉnh sửa
		user.setUpdateAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

		// Lưu lại thông tin người dùng
		userRespository.save(user);
	}

	public UserResponse getInformationUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRespository.findByEmailOrPhone(authentication.getName(), authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		UserResponse response = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(),
				user.getAddress(), null);
		return response;
	}

	// refreshToken
	public UserResponse refreshToken(String refreshToken) {
		if (refreshToken == null) {
			throw new IdException("Bạn không có refresh token ở cookie");
		}
		// check valid token
		Jwt decodedToken = jwtService.checkValidToken(refreshToken);
		String username = decodedToken.getSubject();

		// check refeshToken có phải của user đó
		User user = userRespository.findByEmailOrPhoneAndRefreshToken(username, username, refreshToken)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// danh sách quyền của user
		List<UserRole> userRoles = userRoleRepository.findByUser(user);
		Collection<GrantedAuthority> authorities = userRoles.stream()
				.map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName())).collect(Collectors.toList());

		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

		// create accessToken
		String accessToken = jwtService.createAccessToken(authentication);
		UserResponse loginResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(),
				user.getAddress(), accessToken);

		return loginResponse;

	}

//	 danh sách người dùng
	public Page<InformationAccountResponse> getAccountUsers(int currentPage, int pageSize) {
		Role role = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new UsernameNotFoundException("Role not found"));
		return getAccountsByRole(role, currentPage, pageSize);
	}

	// danh sách nhân viên
	public Page	<InformationAccountResponse> getAccountStaffs(int currentPage, int pageSize) {
		Role role = roleRepository.findByName("ROLE_STAFF")
				.orElseThrow(() -> new UsernameNotFoundException("Role not found"));
		return getAccountsByRole(role, currentPage, pageSize);
	}

	private Page<InformationAccountResponse> getAccountsByRole(Role role, int currentPage, int pageSize) {
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
		Page<UserRole> currentUser = userRoleRepository.findByRole(role, pageable);
		List<InformationAccountResponse> response = new ArrayList<>();

		currentUser.forEach(userRole -> {
			User user = userRole.getUser();
			InformationAccountResponse accountResponse = new InformationAccountResponse();
			accountResponse.setUserId(user.getId());
			accountResponse.setEmail(user.getEmail());
			accountResponse.setAddress(user.getAddress());
			accountResponse.setName(user.getName());
			accountResponse.setPhone(user.getPhone());
			response.add(accountResponse);
		});

		return new PageImpl<>(response, pageable, currentUser.getTotalElements());
	}
	

	// xóa tài khoản nhân
	public void deleteAccountStaff(Long userId) {
		User user = userRespository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("user not found"));
		userRespository.delete(user);
	}

	private User createUser(RegisterUserDto userDto) {
		User user = new User();
		user.setEmail(userDto.getEmail());
		user.setPhone(userDto.getPhone());
		String hashPassword = passwordEncoder.encode(userDto.getPassword());
		user.setPassword(hashPassword);
		user.setCreateAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		return user;
	}

	private void assignRoleToUser(User user, String roleName) {
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new UsernameNotFoundException("Vai trò không tồn tại: " + roleName));
		UserRole userRole = new UserRole();
		userRole.setUser(user);
		userRole.setRole(role);
		userRoleRepository.save(userRole);
	}

	private void createCartForUser(User user) {
		Cart cart = new Cart();
		cart.setUser(user);
		cartRepository.save(cart);
	}

}
