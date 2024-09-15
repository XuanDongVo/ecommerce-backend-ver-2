package xuandong.ecommerce_ver_2.controller.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import xuandong.ecommerce_ver_2.dto.request.OrderRequest;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.service.order.OrderService;

@RestController
@RequestMapping("order")
public class OrderController {
	
	@Autowired
	private OrderService orderService;

	@PostMapping
	public ResponseEntity<RestResponse<String>> order(@RequestBody OrderRequest orderRequest ,  HttpServletRequest request ,HttpServletResponse servletResponse){
		orderService.processOrderItems(orderRequest ,request , servletResponse);
		RestResponse<String> response = new RestResponse<>(HttpStatus.CREATED.value(), null, "Create order is successfully", null);
		return ResponseEntity.ok(response);
	}
}
