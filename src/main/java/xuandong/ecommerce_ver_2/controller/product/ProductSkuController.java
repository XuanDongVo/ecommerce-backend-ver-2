package xuandong.ecommerce_ver_2.controller.product;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import xuandong.ecommerce_ver_2.dto.request.MultipleOptionsProductRequest;
import xuandong.ecommerce_ver_2.dto.response.ProductDetailResponse;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.service.product.ProductSkuService;


@RestController
@RequestMapping("product")
public class ProductSkuController {

	@Autowired
	private ProductSkuService productSkuService;

	@GetMapping("/{id}")
	public ResponseEntity<RestResponse<ProductDetailResponse>> getProductSkues(@PathVariable long id ) {
		ProductDetailResponse productSkus = productSkuService.getSkusById(id);

		RestResponse<ProductDetailResponse> response = new RestResponse<>(HttpStatus.OK.value(), null, 
				"Successfully retrieved product SKUs.", productSkus);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/subcategory")
	public ResponseEntity<RestResponse<Page<ProductDetailResponse>>> getProductSkuesBySubCategory(
			@RequestParam long id , @RequestParam( value = "current" , defaultValue = "1") int currentOptional ,
			@RequestParam(value = "pageSize" , defaultValue =  "20") int pageSizeOptional , @RequestBody(required = false) MultipleOptionsProductRequest options) {
		
		Page<ProductDetailResponse> productSkus = productSkuService.getSkusBySubCategory(id ,currentOptional ,pageSizeOptional ,options);
		RestResponse<Page<ProductDetailResponse>> response = new RestResponse<>(HttpStatus.OK.value(), null, 
				"Successfully retrieved product SKUs.", productSkus);

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/search")
	public ResponseEntity<RestResponse<Page<ProductDetailResponse>>> searchProduct(@RequestParam("search") String search , @RequestParam( value = "current" , defaultValue = "1") int currentOptional ,
			@RequestParam(value = "pageSize"  ,defaultValue =  "20")  int pageSizeOptional ,  @RequestBody(required = false) MultipleOptionsProductRequest multipleOptionsProductRequest) {
		Page<ProductDetailResponse> productSkus = productSkuService.searchProduct(search, currentOptional, pageSizeOptional , multipleOptionsProductRequest);
		RestResponse<Page<ProductDetailResponse>> response = new RestResponse<>(HttpStatus.OK.value(), null, 
				"Successfully retrieved product SKUs.", productSkus);
		 
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/gender")
	public ResponseEntity<RestResponse<Page<ProductDetailResponse>>> getProductSkuesByGender(@RequestParam("gender") String gender , @RequestParam( value = "current" , defaultValue = "1") int currentOptional ,
			@RequestParam(value = "pageSize"  ,defaultValue =  "20")  int pageSizeOptional ,  @RequestBody(required = false) MultipleOptionsProductRequest multipleOptionsProductRequest) {
		Page<ProductDetailResponse> productSkus = productSkuService.getSkusByGender(gender, currentOptional, pageSizeOptional , multipleOptionsProductRequest);
		RestResponse<Page<ProductDetailResponse>> response = new RestResponse<>(HttpStatus.OK.value(), null, 
				"Successfully retrieved product SKUs.", productSkus);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/category")
	public ResponseEntity<RestResponse<Page<ProductDetailResponse>>> getProductSkuesByCategory(
			@RequestParam(name = "category") String name , @RequestParam( value = "current" , defaultValue = "1") int currentOptional ,
			@RequestParam(value = "pageSize" , defaultValue =  "20") int pageSizeOptional , @RequestBody(required = false) MultipleOptionsProductRequest options) {
		
		Page<ProductDetailResponse> productSkus = productSkuService.getSkusByCategory(name ,currentOptional ,pageSizeOptional ,options);
		RestResponse<Page<ProductDetailResponse>> response = new RestResponse<>(HttpStatus.OK.value(), null, 
				"Successfully retrieved product SKUs.", productSkus);

		return ResponseEntity.ok(response);
	}
	
	
	
	
	
}
