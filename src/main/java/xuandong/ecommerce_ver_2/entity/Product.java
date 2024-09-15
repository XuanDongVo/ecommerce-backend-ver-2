package xuandong.ecommerce_ver_2.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "is_delete", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean isDelete;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_category_id", nullable = false)
	private SubCategory subCategory;

	 @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	    private List<ProductColorImg> productColorImgs;

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public List<ProductColorImg> getProductColorImgs() {
		return productColorImgs;
	}

	public void setProductColorImgs(List<ProductColorImg> productColorImgs) {
		this.productColorImgs = productColorImgs;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

}
