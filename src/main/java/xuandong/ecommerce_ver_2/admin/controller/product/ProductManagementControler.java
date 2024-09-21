package xuandong.ecommerce_ver_2.admin.controller.product;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import xuandong.ecommerce_ver_2.dto.request.admin.AddProductRequest;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.service.product.ProductSkuService;

@RestController
@RequestMapping("/admin/product")
@PreAuthorize("hasRole('ADMIN')")
public class ProductManagementControler {

	@Autowired
	private ProductSkuService productSkuService;

	@PostMapping("/add")
	public ResponseEntity<RestResponse<String>> addProduct(@RequestPart("product") String productJson,
			@RequestPart("imageFiles") List<MultipartFile> file) throws URISyntaxException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		AddProductRequest productRequest = objectMapper.readValue(productJson, AddProductRequest.class);
		productSkuService.addProduct(productRequest, file);
		RestResponse<String> response = new RestResponse<>(HttpStatus.OK.value(), null, "Successfully add product",
				null);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<RestResponse<String>> deleteProduct(@RequestParam("name") String nameProduct) {
		productSkuService.deleteProduct(nameProduct);
		RestResponse<String> response = new RestResponse<>(HttpStatus.OK.value(), null, "Successfully Delete product",
				null);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/delete/child")
	public ResponseEntity<RestResponse<String>> deleteProductImageAndColor(@RequestParam("id") Long productId,
			@RequestParam("color") String color) {
		productSkuService.deleteProductImageAndColor(productId, color);
		RestResponse<String> response = new RestResponse<>(HttpStatus.OK.value(), null, "Successfully Delete product",
				null);
		return ResponseEntity.ok(response);
	}

}
