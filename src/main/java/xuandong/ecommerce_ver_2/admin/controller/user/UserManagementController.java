package xuandong.ecommerce_ver_2.admin.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xuandong.ecommerce_ver_2.dto.request.RegisterUserDto;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.dto.response.admin.InformationAccountResponse;
import xuandong.ecommerce_ver_2.service.user.UserService;

@RestController
@RequestMapping("/admin/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<RestResponse<Page<InformationAccountResponse>>> getAccountUsers(
			@RequestParam(value = "current", defaultValue = "1") int currentPage,
			@RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {

		Page<InformationAccountResponse> page = userService.getAccountUsers(currentPage, pageSize);
		RestResponse<Page<InformationAccountResponse>> response = new RestResponse<>(HttpStatus.OK.value(), null,
				"Get account users successfully", page);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/staff")
	public ResponseEntity<RestResponse<Page<InformationAccountResponse>>> getAccountStaffs(
			@RequestParam(value = "current", defaultValue = "1") int currentPage,
			@RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
		Page<InformationAccountResponse> page = userService.getAccountStaffs(currentPage, pageSize);
		RestResponse<Page<InformationAccountResponse>> response = new RestResponse<>(HttpStatus.OK.value(), null,
				"Get account staffs successfully", page);

		return ResponseEntity.ok(response);
	}

	// Đăng ký tài khoản cho staff
	@PostMapping("/staff/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterUserDto registerUserDto) {
		userService.registerStaff(registerUserDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<RestResponse<String>> deleteAccountUser(@PathVariable Long id) {
		userService.deleteAccountStaff(id);
		RestResponse<String> response = new RestResponse<>(HttpStatus.OK.value(), null, "Delete account successfully",
				null);

		return ResponseEntity.ok(response);
	}
}
