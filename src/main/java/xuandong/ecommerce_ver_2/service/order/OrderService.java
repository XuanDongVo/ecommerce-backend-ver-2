package xuandong.ecommerce_ver_2.service.order;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import xuandong.ecommerce_ver_2.dto.request.OrderRequest;
import xuandong.ecommerce_ver_2.dto.response.DetailCartResponse;
import xuandong.ecommerce_ver_2.entity.CartDetail;
import xuandong.ecommerce_ver_2.entity.Inventory;
import xuandong.ecommerce_ver_2.entity.Order;
import xuandong.ecommerce_ver_2.entity.OrderDetail;
import xuandong.ecommerce_ver_2.entity.ProductSku;
import xuandong.ecommerce_ver_2.entity.User;
import xuandong.ecommerce_ver_2.enums.OrderStatus;
import xuandong.ecommerce_ver_2.exception.OutOfStockException;
import xuandong.ecommerce_ver_2.repository.CartDetailRepository;
import xuandong.ecommerce_ver_2.repository.InventoryRepository;
import xuandong.ecommerce_ver_2.repository.OrderDetailRepository;
import xuandong.ecommerce_ver_2.repository.OrderRepository;
import xuandong.ecommerce_ver_2.repository.ProductSkuRepository;
import xuandong.ecommerce_ver_2.repository.UserRespository;

@Service
public class OrderService {

	private OrderRepository orderRepository;
	private UserRespository userRespository;
	private CartDetailRepository cartDetailRepository;
	private OrderDetailRepository orderDetailRepository;
	private InventoryRepository inventoryRepository;
	private ProductSkuRepository productSkuRepository;

	public OrderService(OrderRepository orderRepository, UserRespository userRespository,
			CartDetailRepository cartDetailRepository, OrderDetailRepository orderDetailRepository,
			InventoryRepository inventoryRepository, ProductSkuRepository productSkuRepository) {
		this.orderRepository = orderRepository;
		this.userRespository = userRespository;
		this.cartDetailRepository = cartDetailRepository;
		this.orderDetailRepository = orderDetailRepository;
		this.inventoryRepository = inventoryRepository;
		this.productSkuRepository = productSkuRepository;
	}

	@Transactional
	public void processOrderItems(OrderRequest orderRequest, HttpServletRequest request, HttpServletResponse response) {
		Order order = createOrder(orderRequest);

		// người dùng ẩn danh
		if (order.getUser().getEmail().equals("anonymous")) {
			createOrderDetailForAnonymous(order, orderRequest, request, response);
			return;
		}

		List<CartDetail> cartDetails = cartDetailRepository.findbyIds(orderRequest.getIds());

		// Kiểm tra tồn kho
		List<String> outOfStockProducts = checkStock(cartDetails);
		// Nếu có sản phẩm hết hàng, trả về thông báo lỗi
		if (!outOfStockProducts.isEmpty()) {
			throw new OutOfStockException("Some products are out of stock: " + String.join(", ", outOfStockProducts));
		}
		
		cartDetails.forEach(cartDetail -> {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(order);
			orderDetail.setProductSku(cartDetail.getProductSku());
			orderDetail.setQuantity(cartDetail.getQuantity());

			// cập nhật hàng tồn kho trong kho hàng
			Inventory invetory = inventoryRepository.findByProductSkus(cartDetail.getProductSku()).get();
			invetory.setStock(invetory.getStock() - cartDetail.getQuantity());

			orderDetailRepository.save(orderDetail);
			inventoryRepository.save(invetory);
			cartDetailRepository.delete(cartDetail);
		});

	}

	private Order createOrder(OrderRequest orderRequest) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Order order = new Order();
		// Không phải người dùng ẩn danh
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			// lay ra nguoi dung hien tai dang dang nhap
			String username = authentication.getName();
			User user = userRespository.findByEmailOrPhone(username, username)
					.orElseThrow(() -> new UsernameNotFoundException("user not found"));
			order.setUser(user);
		} else {
			// Gán người dùng ẩn danh
			User anonymousUser = userRespository.findByEmail("anonymous")
					.orElseThrow(() -> new RuntimeException("Anonymous user not found"));
			order.setUser(anonymousUser);
		}
		order.setCustomerAddress(orderRequest.getAddress());
		order.setCustomerName(orderRequest.getCustomerName());
		order.setCustomerEmail(orderRequest.getCustomerEmail());
		order.setCustomerPhone(orderRequest.getCustomerPhone());
		order.setTotalPrice(orderRequest.getTotalPrice());
		order.setCreateAt(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		order.setOrderStatus(OrderStatus.PENDING);

		orderRepository.save(order);

		return order;
	}

	private List<String> checkStock(List<CartDetail> CartDetail) {
		List<String> outOfStockProducts = new ArrayList<>();

		for (CartDetail item : CartDetail) {
			int stock = inventoryRepository.findByProductSkus(item.getProductSku()).get().getStock();
			if (item.getQuantity() > stock) {
				outOfStockProducts.add(item.getProductSku().getProductColorImg().getProduct().getName());
			}
		}

		return outOfStockProducts;
	}

	@Transactional
	private void createOrderDetailForAnonymous(Order order, OrderRequest orderRequest, HttpServletRequest request,
			HttpServletResponse response) {
		// Lấy giỏ hàng từ cookie hiện tại (nếu có)
		List<DetailCartResponse> detailCarts = new ArrayList<>();

		String cartCookieValue = getCookieValue(request, "cart");

		// Nếu đã có cookie giỏ hàng, chuyển đổi chuỗi JSON từ cookie thành danh sách
		// CartDetail
		if (cartCookieValue != null && !cartCookieValue.isEmpty()) {
			detailCarts = decodeCartDetailsFromCookie(cartCookieValue);
		}

		// Lọc detailCarts dựa vào ids trong orderRequest
		List<Long> ids = orderRequest.getIds();

		// Lọc detailCarts mà id có trong danh sách ids của orderRequest
		List<DetailCartResponse> filteredDetailCarts = detailCarts.stream().filter(cart -> ids.contains(cart.getId()))
				.toList();

		// Kiểm tra tồn kho
		List<String> outOfStockProducts = new ArrayList<>();

		for (DetailCartResponse item : filteredDetailCarts) {
			ProductSku productSku = productSkuRepository.findById(item.getId())
					.orElseThrow(() -> new UsernameNotFoundException("Product sku not found"));
			int stock = inventoryRepository.findByProductSkus(productSku).get().getStock();
			if (item.getQuantity() > stock) {
				outOfStockProducts.add(productSku.getProductColorImg().getProduct().getName());
			}
		}

		if (!outOfStockProducts.isEmpty()) {
			throw new OutOfStockException("Some products are out of stock: " + String.join(", ", outOfStockProducts));
		}

		// tạo order Detail cho khach hang
		filteredDetailCarts.forEach(cartDetail -> {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(order);

			ProductSku productSku = productSkuRepository.findById(cartDetail.getId())
					.orElseThrow(() -> new UsernameNotFoundException("Product sku not found"));

			orderDetail.setProductSku(productSku);
			orderDetail.setQuantity(cartDetail.getQuantity());

			// cập nhật hàng tồn kho trong kho hàng
			Inventory invetory = inventoryRepository.findByProductSkus(productSku).get();
			invetory.setStock(invetory.getStock() - cartDetail.getQuantity());
			orderDetailRepository.save(orderDetail);
			inventoryRepository.save(invetory);
		});

		detailCarts.removeAll(filteredDetailCarts);

		// Kiểm tra nếu giỏ hàng rỗng
		if (detailCarts.isEmpty()) {
			// Nếu giỏ hàng trống, xóa cookie "cart"
			ResponseCookie emptyCartCookie = ResponseCookie.from("cart", "").httpOnly(true).secure(true)
					.sameSite("Strict").path("/").maxAge(0).build();
			response.addHeader("Set-Cookie", emptyCartCookie.toString());
		} else {
			// Nếu giỏ hàng vẫn còn, cập nhật cookie với giỏ hàng mới
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				// Chuyển danh sách CartDetail còn lại thành chuỗi JSON
				String cartDetailsJson = objectMapper.writeValueAsString(detailCarts);
				// Mã hóa chuỗi JSON thành Base64
				String encodedCartDetails = Base64.getEncoder().encodeToString(cartDetailsJson.getBytes());

				// Tạo cookie với thời gian sống là 1 tuần
				ResponseCookie updatedCookie = ResponseCookie.from("cart", encodedCartDetails).httpOnly(true)
						.secure(true).sameSite("Strict").path("/").maxAge(7 * 24 * 60 * 60).build();

				// Thêm cookie vào response
				response.addHeader("Set-Cookie", updatedCookie.toString());
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error converting CartDetail list to JSON", e);
			}
		}

	}

	// Phương thức lấy giá trị từ cookie
	private String getCookieValue(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// Giải mã
	private List<DetailCartResponse> decodeCartDetailsFromCookie(String cartCookieValue) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<DetailCartResponse> detailCarts = new ArrayList<>();

		try {
			// Giải mã chuỗi JSON từ cookie (Base64)
			byte[] decodedBytes = Base64.getDecoder().decode(cartCookieValue);
			String decodedCartJson = new String(decodedBytes);

			// Chuyển đổi chuỗi JSON thành danh sách DetailCartResponse
			detailCarts = objectMapper.readValue(decodedCartJson, new TypeReference<List<DetailCartResponse>>() {
			});
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error parsing cart details from cookie", e);
		}

		return detailCarts;
	}

}
