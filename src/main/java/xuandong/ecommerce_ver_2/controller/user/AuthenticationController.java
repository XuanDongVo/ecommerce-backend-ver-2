package xuandong.ecommerce_ver_2.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import xuandong.ecommerce_ver_2.dto.request.LoginUser;
import xuandong.ecommerce_ver_2.dto.request.RegisterUserDto;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.dto.response.UserResponse;
import xuandong.ecommerce_ver_2.service.user.UserService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	private UserService userService;

	public AuthenticationController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterUserDto registerUserDto) {
		userService.registerUser(registerUserDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/login")
	public ResponseEntity<RestResponse<UserResponse>> loginUser(@RequestBody LoginUser loginUser,
			HttpServletResponse response) {
		RestResponse<UserResponse> loginResponse = userService.loginUser(loginUser, response);
		return ResponseEntity.ok(loginResponse);
	}

	@GetMapping("/logout")
	public ResponseEntity<RestResponse<String>> logout(HttpServletResponse response) {
		userService.logout(response);
		RestResponse<String> restResponse = new RestResponse<String>(HttpStatus.OK.value(), null,
				"log out is successfully", null);
		return ResponseEntity.ok(restResponse);
	}

	@GetMapping("/refresh")
	public ResponseEntity<RestResponse<UserResponse>> getRefreshToken(
			@CookieValue(name = "refresh_token") String token) {
		UserResponse userResponse = userService.refreshToken(token);
		RestResponse<UserResponse> response = new RestResponse<UserResponse>(HttpStatus.OK.value(),null,
				"Refresh Token successfully", userResponse);
		return ResponseEntity.ok(response);
	}

}
