package xuandong.ecommerce_ver_2.dto.response;

import java.util.List;

public class CategoryResponse {
    private Long id;
    private String category;
    private String gender;
    private List<SubCategoryResponse> subCategories; 

    public CategoryResponse() {
        super();
    }

    public CategoryResponse(Long id, String category, String gender, List<SubCategoryResponse> subCategories) {
		super();
		this.id = id;
		this.category = category;
		this.gender = gender;
		this.subCategories = subCategories;
	}

	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

  

    public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public List<SubCategoryResponse> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategoryResponse> subCategories) {
        this.subCategories = subCategories;
    }

    
}
