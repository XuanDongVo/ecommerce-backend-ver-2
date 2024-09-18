package xuandong.ecommerce_ver_2.dto.request.admin;

import java.util.List;


public class AddProductRequest {
	private String nameProduct;
	private String description;
	private String nameSubCategory;
	private List<ProductVariations> productVariations; // Danh sách biến thể sản phẩm

	public AddProductRequest() {
	}

	public AddProductRequest(String nameProduct, String description, String nameSubCategory,
			List<ProductVariations> productVariations) {
		this.nameProduct = nameProduct;
		this.description = description;
		this.nameSubCategory = nameSubCategory;
		this.productVariations = productVariations;
	}

	// Getters and Setters
	public String getNameProduct() {
		return nameProduct;
	}

	public void setNameProduct(String nameProduct) {
		this.nameProduct = nameProduct;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNameSubCategory() {
		return nameSubCategory;
	}

	public void setNameSubCategory(String nameSubCategory) {
		this.nameSubCategory = nameSubCategory;
	}

	public List<ProductVariations> getProductVariations() {
		return productVariations;
	}

	public void setProductVariations(List<ProductVariations> productVariations) {
		this.productVariations = productVariations;
	}

}
