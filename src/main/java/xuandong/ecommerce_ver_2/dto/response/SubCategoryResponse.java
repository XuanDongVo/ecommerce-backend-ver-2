package xuandong.ecommerce_ver_2.dto.response;

public class SubCategoryResponse {
	public Long id;
	public String name;

	public SubCategoryResponse() {
	}
	
	public SubCategoryResponse(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
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
	
	
}