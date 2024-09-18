package xuandong.ecommerce_ver_2.dto.request.admin;

import java.util.List;

public class ProductVariations {
	private String color; // Màu sắc của biến thể
	private List<ProductSkus> productSkus; // Danh sách SKU cho biến thể

	public ProductVariations() {
	}

	public ProductVariations(String color, List<ProductSkus> productSkus) {
		this.color = color;
		this.productSkus = productSkus;
	}

	// Getters and Setters
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
