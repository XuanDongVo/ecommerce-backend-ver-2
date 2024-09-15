package xuandong.ecommerce_ver_2.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_sku")
public class ProductSku {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_color_img_id", nullable = false)
	private ProductColorImg productColorImg;

	@Column
	private double price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "size_id", nullable = false)
	private Size size;

	@OneToOne(mappedBy = "productSkus", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private Inventory inventory;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductColorImg getProductColorImg() {
		return productColorImg;
	}

	public void setProductColorImg(ProductColorImg productColorImg) {
		this.productColorImg = productColorImg;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

}
