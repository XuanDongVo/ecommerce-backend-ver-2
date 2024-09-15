package xuandong.ecommerce_ver_2.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterUserDto {
	@Email
	private String email;
	@Pattern(regexp = "^\\d{11}$", message = "Số điện thoại phải chứa 11 chữ số.")
	private String phone;
	@Size(min = 3, message = "Mật khẩu phải có ít nhất 3 ký tự.")
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
