package xuandong.ecommerce_ver_2.service.cartDetail;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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
import xuandong.ecommerce_ver_2.dto.request.AddProductInCartRequest;
import xuandong.ecommerce_ver_2.dto.request.ModifyProductRequest;
import xuandong.ecommerce_ver_2.dto.response.DetailCartResponse;
import xuandong.ecommerce_ver_2.entity.Cart;
import xuandong.ecommerce_ver_2.entity.CartDetail;
import xuandong.ecommerce_ver_2.entity.Product;
import xuandong.ecommerce_ver_2.entity.ProductColorImg;
import xuandong.ecommerce_ver_2.entity.ProductSku;
import xuandong.ecommerce_ver_2.entity.Size;
import xuandong.ecommerce_ver_2.entity.User;
import xuandong.ecommerce_ver_2.exception.IdException;
import xuandong.ecommerce_ver_2.repository.CartDetailRepository;
import xuandong.ecommerce_ver_2.repository.CartRepository;
import xuandong.ecommerce_ver_2.repository.ProductColorImgRepository;
import xuandong.ecommerce_ver_2.repository.ProductSkuRepository;
import xuandong.ecommerce_ver_2.repository.SizeRepository;
import xuandong.ecommerce_ver_2.repository.UserRespository;

@Service
public class CartDetailService {
	private CartDetailRepository cartDetailRepository;
	private ProductSkuRepository productSkuRepository;
	private ProductColorImgRepository productColorImgRepository;
	private SizeRepository sizeRepository;
	private CartRepository cartRepository;
	private UserRespository userRespository;

	public CartDetailService(CartDetailRepository cartDetailRepository, ProductSkuRepository productSkuRepository,
			ProductColorImgRepository productColorImgRepository, SizeRepository sizeRepository,
			CartRepository cartRepository, UserRespository userRespository) {
		this.cartDetailRepository = cartDetailRepository;
		this.productSkuRepository = productSkuRepository;
		this.productColorImgRepository = productColorImgRepository;
		this.sizeRepository = sizeRepository;
		this.cartRepository = cartRepository;
		this.userRespository = userRespository;
	}

	public List<DetailCartResponse> getDetailsInCart(HttpServletRequest request) {

		// Tìm giỏ hàng của người dùng hiện tại
		Cart cart = getUserCart();

		// Nếu giỏ hàng không tồn tại, tìm trong cookie
		if (cart == null) {
			return getCartDetailsFromCookie(request);
		}

		// Lấy chi tiết giỏ hàng từ database
		return getCartDetailsFromDatabase(cart);
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

	@Transactional
	public void addProductToCartDetail(AddProductInCartRequest productRequest, HttpServletResponse response,
			HttpServletRequest request) {
		ProductColorImg productColorImg = productColorImgRepository.findById(productRequest.getId())
				.orElseThrow(() -> new IdException("ProductColorImg not found by id " + productRequest.getId()));
		Size size = sizeRepository.findByName(productRequest.getSize())
				.orElseThrow(() -> new UsernameNotFoundException("Size not found by name " + productRequest.getSize()));

		ProductSku productSku = productSkuRepository.findByProductColorImgAndSize(productColorImg, size)
				.orElseThrow(() -> new UsernameNotFoundException("not found"));

		// Tìm giỏ hàng của người dùng hiện tại
		Cart cart = getUserCart();
		if (cart == null) {
			cartDetailForAnonymous(productSku, productRequest, request, response);
			return;
		}

		CartDetail cartDetail = cartDetailRepository.findByProductSkuAndCart(productSku, cart)
				.orElseGet(() -> new CartDetail());

		// Cập nhật số lượng sản phẩm trong CartDetail
		int updatedQuantity = cartDetail.getId() != null ? cartDetail.getQuantity() + productRequest.getQuantity()
				: productRequest.getQuantity();
		cartDetail.setQuantity(updatedQuantity);
		cartDetail.setCart(cart);
		cartDetail.setProductSku(productSku);

		cartDetailRepository.save(cartDetail);
	}

	private void cartDetailForAnonymous(ProductSku productSku, AddProductInCartRequest productRequest,
			HttpServletRequest request, HttpServletResponse response) {
		// Lấy giỏ hàng từ cookie hiện tại (nếu có)
		List<DetailCartResponse> detailCarts = new ArrayList<>();

		String cartCookieValue = getCookieValue(request, "cart");

		// Nếu đã có cookie giỏ hàng, chuyển đổi chuỗi JSON từ cookie thành danh sách
		// CartDetail
		if (cartCookieValue != null && !cartCookieValue.isEmpty()) {
			detailCarts = decodeCartDetailsFromCookie(cartCookieValue);
		}

		Optional<DetailCartResponse> existingCartDetail = detailCarts.stream()
				.filter(cd -> cd.getId().equals(productSku.getId())).findFirst();

		if (existingCartDetail.isPresent()) {
			// Nếu sản phẩm đã tồn tại trong giỏ hàng, cập nhật số lượng
			DetailCartResponse detailCartResponse = existingCartDetail.get();
			int updatedQuantity = detailCartResponse.getQuantity() + productRequest.getQuantity();
			detailCartResponse.setQuantity(updatedQuantity);
		} else {
			ProductColorImg productColorImg = productSku.getProductColorImg();
			Product product = productColorImg.getProduct();

			// Nếu sản phẩm chưa có trong giỏ hàng, tạo CartDetail mới
			DetailCartResponse detailCart = new DetailCartResponse(productSku.getId(), product.getName(),
					productColorImg.getImage(), productColorImg.getColor().getName(), productSku.getSize().getName(),
					productRequest.getQuantity(), productSku.getPrice());
			detailCarts.add(detailCart);
		}

		updateCartCookie(response, detailCarts);

	}

	public void modifyProductInCartDetail(ModifyProductRequest modifyProductRequest, HttpServletRequest request,
			HttpServletResponse response) {
		Cart cart = getUserCart();
		// xử lí người dùng ẩn danh
		if (cart == null) {
			modifyProductForAnonymous(modifyProductRequest, request, response);
			return;
		}

		CartDetail cartDetail = cartDetailRepository.findById(modifyProductRequest.getId())
				.orElseThrow(() -> new IdException("Cart Detail not found by id " + modifyProductRequest.getId()));
		cartDetail.setQuantity(modifyProductRequest.getQuantity());
		cartDetailRepository.save(cartDetail);
	}

	public void removeProductInCartDetail(Long id, HttpServletRequest request, HttpServletResponse response) {
		Cart cart = getUserCart();
		// xử lí người dùng ẩn danh
		if (cart == null) {
			removeProductForAnonymous(id, request, response);
			return;
		}
		CartDetail cartDetail = cartDetailRepository.findById(id)
				.orElseThrow(() -> new IdException("Cart Detail not found by id " + id));
		cartDetailRepository.delete(cartDetail);
	}

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

	private Cart getUserCart() {
		// Lấy thông tin người dùng hiện tại từ SecurityContext
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		String username = authentication.getName();
		User user = userRespository.findByEmailOrPhone(username, username).get();
		// lấy ra cart
		Cart cart = cartRepository.findByUser(user).get();
		return cart;
	}

	private List<DetailCartResponse> getCartDetailsFromCookie(HttpServletRequest request) {
		// Lấy giỏ hàng từ cookies nếu không tìm thấy trong cơ sở dữ liệu
		List<DetailCartResponse> detailCartResponses = new ArrayList<>();
		String cartCookieValue = getCookieValue(request, "cart");

		if (cartCookieValue != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				// Giải mã chuỗi Base64 từ cookie
				byte[] decodedBytes = Base64.getDecoder().decode(cartCookieValue);
				String decodedCartJson = new String(decodedBytes);

				// Chuyển đổi JSON thành List<DetailCartResponse>
				List<DetailCartResponse> cartDetails = objectMapper.readValue(decodedCartJson,
						new TypeReference<List<DetailCartResponse>>() {
						});

				// Thêm các chi tiết giỏ hàng từ cookie vào danh sách
				detailCartResponses.addAll(cartDetails);
			} catch (Exception e) {
				// Xử lý lỗi nếu không thể giải mã hoặc parse JSON
				throw new RuntimeException("Error parsing cart details from cookies", e);
			}
		}

		return detailCartResponses;
	}

	private List<DetailCartResponse> getCartDetailsFromDatabase(Cart cart) {
		// Lấy danh sách chi tiết giỏ hàng từ cơ sở dữ liệu dựa trên giỏ hàng của người
		// dùng
		List<DetailCartResponse> detailCartResponses = new ArrayList<>();
		List<CartDetail> cartDetails = cartDetailRepository.findByCart(cart);

		// Duyệt qua từng chi tiết giỏ hàng từ database và chuyển đổi thành
		// DetailCartResponse
		for (CartDetail cartDetail : cartDetails) {
			ProductSku productSku = cartDetail.getProductSku();
			ProductColorImg productColorImg = productSku.getProductColorImg();
			Product product = productColorImg.getProduct();

			// Tạo đối tượng DetailCartResponse và thêm vào danh sách
			DetailCartResponse response = new DetailCartResponse(cartDetail.getId(), product.getName(),
					productColorImg.getImage(), productColorImg.getColor().getName(), productSku.getSize().getName(),
					cartDetail.getQuantity(), cartDetail.getProductSku().getPrice());

			detailCartResponses.add(response);
		}

		return detailCartResponses;
	}

	private void modifyProductForAnonymous(ModifyProductRequest modifyProductRequest, HttpServletRequest request,
			HttpServletResponse response) {
		List<DetailCartResponse> detailCarts;

		String cartCookieValue = getCookieValue(request, "cart");

		// Nếu đã có cookie giỏ hàng, chuyển đổi chuỗi JSON từ cookie thành danh sách
		// CartDetail
		detailCarts = decodeCartDetailsFromCookie(cartCookieValue);

		// lọc lấy sản phẩm cần modify
		Optional<DetailCartResponse> existingDetailCart = detailCarts.stream()
				.filter(cd -> cd.getId().equals(modifyProductRequest.getId())).findFirst();

		if (existingDetailCart.isPresent()) {
			// Nếu tìm thấy sản phẩm, cập nhật số lượng
			existingDetailCart.get().setQuantity(modifyProductRequest.getQuantity());
		} else {
			throw new RuntimeException("Product not found in cart for modification.");
		}

		// cập nhật dữ liệu trong cookie
		updateCartCookie(response, detailCarts);
	}

	// cập nhật dữ liệu trong cookie
	private void updateCartCookie(HttpServletResponse response, List<DetailCartResponse> detailCarts) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			// Chuyển danh sách CartDetail thành chuỗi JSON
			String cartDetailsJson = objectMapper.writeValueAsString(detailCarts);
			// Mã hóa chuỗi JSON thành Base64
			String encodedCartDetails = Base64.getEncoder().encodeToString(cartDetailsJson.getBytes());

			// Tạo cookie với thời gian sống là 1 tuần
			ResponseCookie cookie = ResponseCookie.from("cart", encodedCartDetails).httpOnly(true).secure(true)
					.sameSite("Strict").path("/").maxAge(7 * 24 * 60 * 60).build();

			// Thêm cookie vào response
			response.addHeader("Set-Cookie", cookie.toString());
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error converting CartDetail list to JSON", e);
		}
	}

	private void removeProductForAnonymous(Long id, HttpServletRequest request, HttpServletResponse response) {
		List<DetailCartResponse> detailCarts;

		String cartCookieValue = getCookieValue(request, "cart");

		// Nếu đã có cookie giỏ hàng, chuyển đổi chuỗi JSON từ cookie thành danh sách
		// CartDetail
		detailCarts = decodeCartDetailsFromCookie(cartCookieValue);

		// lọc lấy sản phẩm cần modify
		Optional<DetailCartResponse> existingDetailCart = detailCarts.stream()
				.filter(cd -> cd.getId().equals(id)).findFirst();

		if (existingDetailCart.isPresent()) {
			// xóa sản phẩm
			detailCarts.remove(existingDetailCart.get());
		} else {
			throw new RuntimeException("Product not found in cart for modification.");
		}

		// cập nhật dữ liệu trong cookie
		updateCartCookie(response, detailCarts);
	}
}
