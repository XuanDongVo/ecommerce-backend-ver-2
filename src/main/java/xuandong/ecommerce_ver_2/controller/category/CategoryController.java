package xuandong.ecommerce_ver_2.controller.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xuandong.ecommerce_ver_2.dto.response.CategoryResponse;
import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.service.category.CategoryService;

@RestController
@RequestMapping("category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	 @GetMapping
	    public ResponseEntity<RestResponse<List<CategoryResponse>>> getCategories() {
	        List<CategoryResponse> categories = categoryService.getCategories();
	        
	        RestResponse<List<CategoryResponse>> restResponse = new RestResponse<>();
	        restResponse.setStatusCode(HttpStatus.OK.value());
	        restResponse.setData(categories);
	        restResponse.setMessage("Categories retrieved successfully");

	        return ResponseEntity.ok(restResponse);
	    }
}
