package xuandong.ecommerce_ver_2.dto.request.admin;

import java.util.List;


public class AddProductRequest {
	private String nameProduct;
	private String description;
	private String nameSubCategory;
	private String color;
	private List<ProductSkus> productSkus;
	
	

	public AddProductRequest() {
		super();
	}

	public AddProductRequest(String nameProduct, String description, String nameSubCategory, String color,
			List<ProductSkus> productSkus) {
		this.nameProduct = nameProduct;
		this.description = description;
		this.nameSubCategory = nameSubCategory;
		this.color = color;
		this.productSkus = productSkus;
	}

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<ProductSkus> getProductSkus() {
		return productSkus;
	}

	public void setProductSkus(List<ProductSkus> productSkus) {
		this.productSkus = productSkus;
	}

}
