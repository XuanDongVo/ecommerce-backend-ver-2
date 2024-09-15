package xuandong.ecommerce_ver_2.controller.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xuandong.ecommerce_ver_2.dto.request.AddProductInCartRequest;
import xuandong.ecommerce_ver_2.dto.request.ModifyProductRequest;
import xuandong.ecommerce_ver_2.dto.response.DetailCartResponse;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.service.cartDetail.CartDetailService;

@RestController
@RequestMapping("cartdetail")
public class CartDetailController {

	@Autowired
	private CartDetailService cartDetailService;

	@GetMapping
	public ResponseEntity<RestResponse<List<DetailCartResponse>>> getDetailsInCart(HttpServletRequest request) {
		List<DetailCartResponse> detailCartResponses = cartDetailService.getDetailsInCart(request);
		RestResponse<List<DetailCartResponse>> response = new RestResponse<List<DetailCartResponse>>(
				HttpStatus.OK.value(), null, "Get Details Cart", detailCartResponses);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/add")
	public ResponseEntity<RestResponse<String>> addProductToCart(@RequestBody AddProductInCartRequest addProductRequest,
			HttpServletResponse servletResponse, HttpServletRequest request) {
		cartDetailService.addProductToCartDetail(addProductRequest, servletResponse, request);

		RestResponse<String> response = new RestResponse<String>(HttpStatus.CREATED.value(), null,
				"Product added to cart successfully", null);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/modify")
	public ResponseEntity<RestResponse<String>> modifyProductInCartDetail(
			@RequestBody ModifyProductRequest modifyProductRequest, HttpServletRequest request,
			HttpServletResponse servletResponse) {
		cartDetailService.modifyProductInCartDetail(modifyProductRequest, request, servletResponse);
		RestResponse<String> response = new RestResponse<String>(HttpStatus.CREATED.value(), null,
				"Modify Product In Cart Detail successfully", null);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/remove/{id}")
	public ResponseEntity<RestResponse<String>> modifyProductInCartDetail(@PathVariable Long id,
			HttpServletRequest request, HttpServletResponse servletResponse) {
		cartDetailService.removeProductInCartDetail(id, request, servletResponse);
		RestResponse<String> response = new RestResponse<String>(HttpStatus.OK.value(), null,
				"Remove Product In Cart Detail successfully", null);
		return ResponseEntity.ok(response);
	}

}
