package xuandong.ecommerce_ver_2.dto.response;

public class OrderDetailResponse {
	private String nameProduct;
	private String pathImage;
	private String size;
	private double price;
	private int quantity;
	private double totalPrice;

	public OrderDetailResponse() {
	}

	public OrderDetailResponse(String nameProduct, String pathImage, String size, double price, int quantity,
			double totalPrice) {
		this.nameProduct = nameProduct;
		this.pathImage = pathImage;
		this.size = size;
		this.price = price;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
	}

	public String getNameProduct() {
		return nameProduct;
	}

	public void setNameProduct(String nameProduct) {
		this.nameProduct = nameProduct;
	}

	public String getPathImage() {
		return pathImage;
	}

	public void setPathImage(String pathImage) {
		this.pathImage = pathImage;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

}
