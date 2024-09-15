package xuandong.ecommerce_ver_2.service.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import xuandong.ecommerce_ver_2.dto.response.CategoryResponse;
import xuandong.ecommerce_ver_2.dto.response.SubCategoryResponse;
import xuandong.ecommerce_ver_2.entity.Category;
import xuandong.ecommerce_ver_2.entity.SubCategory;
import xuandong.ecommerce_ver_2.repository.CategoryRepository;
import xuandong.ecommerce_ver_2.repository.SubCategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final SubCategoryRepository subCategoryRepository;

	public CategoryService(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
		this.categoryRepository = categoryRepository;
		this.subCategoryRepository = subCategoryRepository;
	}

	public List<CategoryResponse> getCategories() {
		List<Category> categories = categoryRepository.findAll();
		List<CategoryResponse> responseList = new ArrayList<>();

		for (Category category : categories) {
			List<SubCategory> subCategories = subCategoryRepository.findByCategory(category);
			List<SubCategoryResponse> subCategoryResponses = new ArrayList<>();

			for (SubCategory subCategory : subCategories) {
				subCategoryResponses.add(new SubCategoryResponse(subCategory.getId(), subCategory.getName()));
			}

			CategoryResponse cateogyResponse = new CategoryResponse(category.getId(), category.getName(),
					category.getGender().getName(), subCategoryResponses);
			responseList.add(cateogyResponse);
		}

		return responseList;
	}

}
