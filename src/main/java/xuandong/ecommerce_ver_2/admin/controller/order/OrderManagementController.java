package xuandong.ecommerce_ver_2.admin.controller.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xuandong.ecommerce_ver_2.dto.request.admin.OrderStatusRequest;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.service.order.OrderService;

@RestController
@RequestMapping("/admin/order")
public class OrderManagementController {
	@Autowired
	private OrderService orderService;
	
	@PostMapping("/modify")
	public ResponseEntity<RestResponse<String>> modifyOrderStatus(@RequestBody OrderStatusRequest orderStatusRequest){
		orderService.modifyOrderStatus(orderStatusRequest);
		RestResponse<String> response = new RestResponse<>(HttpStatus.CREATED.value(), null, "Modify order status is successfully", null);
		return ResponseEntity.ok(response);
	}
}
