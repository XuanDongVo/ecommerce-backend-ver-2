package xuandong.ecommerce_ver_2.service.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import xuandong.ecommerce_ver_2.dto.response.SubCategoryResponse;
import xuandong.ecommerce_ver_2.entity.Category;
import xuandong.ecommerce_ver_2.entity.SubCategory;
import xuandong.ecommerce_ver_2.repository.CategoryRepository;
import xuandong.ecommerce_ver_2.repository.SubCategoryRepository;

@Service
public class SubCategoryService {
	private SubCategoryRepository subCategoryRepository;
	private CategoryRepository categoryRepository;

	public SubCategoryService(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository) {
		this.subCategoryRepository = subCategoryRepository;
		this.categoryRepository = categoryRepository;
	}

	public List<SubCategoryResponse> getSubCategoiesByCategory(String categoryName) {
		Category category = categoryRepository.findByName(categoryName)
				.orElseThrow(() -> new UsernameNotFoundException("Category not found by " + categoryName));

		List<SubCategory> subCategories = subCategoryRepository.findByCategory(category);
		List<SubCategoryResponse> response = new ArrayList<>();

		subCategories.forEach(
				(subCategory) -> response.add(new SubCategoryResponse(subCategory.getId(), subCategory.getName())));
		return response;
	}
}
