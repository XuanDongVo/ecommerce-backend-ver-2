package xuandong.ecommerce_ver_2.dto.request.admin;

import xuandong.ecommerce_ver_2.enums.OrderStatus;

public class OrderStatusRequest {
	private Long orderId;
	private OrderStatus status;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

}
