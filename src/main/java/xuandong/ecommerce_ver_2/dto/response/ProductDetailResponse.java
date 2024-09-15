package xuandong.ecommerce_ver_2.dto.response;

import java.util.List;
import java.util.Map;

public class ProductDetailResponse {
	private Long id;
	private String name;
	private String subCategory;
	private double price;

	private List<ProductSkuResponse> productSkus;

	public ProductDetailResponse() {
	}

	public ProductDetailResponse(Long id, String name, String subCategory, List<ProductSkuResponse> productSkus,
			double price) {
		this.id = id;
		this.name = name;
		this.subCategory = subCategory;
		this.price = price;
		this.productSkus = productSkus;
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public List<ProductSkuResponse> getProductSkus() {
		return productSkus;
	}

	public void setProductSkus(List<ProductSkuResponse> productSkus) {
		this.productSkus = productSkus;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public static class ProductSkuResponse {
		private Long skuId;
		private String color;
		private String img;
		private Map<String, Integer> sizeAndStock;

		public ProductSkuResponse() {
		}

		public ProductSkuResponse(Long skuId,String color, String img, Map<String, Integer> sizeAndStock) {
			this.skuId = skuId;
			this.color = color;
			this.img = img;
			this.sizeAndStock = sizeAndStock;
		}
		

		public Long getSkuId() {
			return skuId;
		}

		public void setSkuId(Long skuId) {
			this.skuId = skuId;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}

		public Map<String, Integer> getSizeAndStock() {
			return sizeAndStock;
		}

		public void setSizeAndStock(Map<String, Integer> sizeAndStock) {
			this.sizeAndStock = sizeAndStock;
		}

	
	}
}
