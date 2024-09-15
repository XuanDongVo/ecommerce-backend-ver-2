package xuandong.ecommerce_ver_2.dto.request.admin;

public class ProductSkus {
	private String size;
	private double price;
	private int stock;

	public ProductSkus() {
	}

	public ProductSkus(String size, double price, int stock) {
		this.size = size;
		this.price = price;
		this.stock = stock;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

}
