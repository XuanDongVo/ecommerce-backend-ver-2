package xuandong.ecommerce_ver_2.dto.response;

public class DetailCartResponse {
	private Long id;
	private String name;
	private String image;
	private String color;
	private String size;
	private int quantity;
	private double price;
	
	
	public DetailCartResponse() {
	}
	public DetailCartResponse(Long id, String name, String image, String color, String size, int quantity,
			double price) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.color = color;
		this.size = size;
		this.quantity = quantity;
		this.price = price;
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	
}
