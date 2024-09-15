package xuandong.ecommerce_ver_2.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xuandong.ecommerce_ver_2.dto.request.UpdateUserRequest;
import xuandong.ecommerce_ver_2.dto.response.UserResponse;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.service.user.UserService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("user")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<RestResponse<UserResponse>> getInformationUser() {
		UserResponse userResponse = userService.getInformationUser();
		RestResponse<UserResponse> response = new RestResponse<UserResponse>(HttpStatus.OK.value(), null,
				"get information user is successfully", userResponse);
		return ResponseEntity.ok(response);
	}


	@PostMapping("/update")
	public ResponseEntity<RestResponse<String>> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
		userService.updateAccount(updateUserRequest);
		RestResponse<String> response = new RestResponse<String>(HttpStatus.CREATED.value(), null,
				"update user is successfully", null);
		return ResponseEntity.ok(response);
	}
	
	

}
