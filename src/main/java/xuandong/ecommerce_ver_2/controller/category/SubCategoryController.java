package xuandong.ecommerce_ver_2.controller.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xuandong.ecommerce_ver_2.dto.response.RestResponse;
import xuandong.ecommerce_ver_2.dto.response.SubCategoryResponse;
import xuandong.ecommerce_ver_2.service.category.SubCategoryService;

@RestController
@RequestMapping("subcategory")
public class SubCategoryController {
	@Autowired
	private SubCategoryService subCategoryService;

	@GetMapping("/{categoryName}")
	public ResponseEntity<RestResponse<List<SubCategoryResponse>>> getSubCategoiesByCategory(@PathVariable String categoryName ){
		List<SubCategoryResponse> data = subCategoryService.getSubCategoiesByCategory(categoryName);
		RestResponse<List<SubCategoryResponse>> response = new RestResponse<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage(" List SubCategory by Catgory");
		response.setData(data);
		return ResponseEntity.ok(response);
	}
}
