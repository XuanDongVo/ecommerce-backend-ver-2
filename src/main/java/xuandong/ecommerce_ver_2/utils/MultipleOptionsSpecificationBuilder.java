package xuandong.ecommerce_ver_2.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import xuandong.ecommerce_ver_2.dto.request.MultipleOptionsProductRequest;
import xuandong.ecommerce_ver_2.entity.Category;
import xuandong.ecommerce_ver_2.entity.Color;
import xuandong.ecommerce_ver_2.entity.Gender;
import xuandong.ecommerce_ver_2.entity.Product;
import xuandong.ecommerce_ver_2.entity.ProductColorImg;
import xuandong.ecommerce_ver_2.entity.ProductSku;
import xuandong.ecommerce_ver_2.entity.Size;
import xuandong.ecommerce_ver_2.entity.SubCategory;

public class MultipleOptionsSpecificationBuilder {

	public static Specification<Product> hasMultipleOptions( MultipleOptionsProductRequest options) {
	    return (root, query, criteriaBuilder) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        // Áp dụng bộ lọc 
	        predicates.add(hasColor(options.getColor()).toPredicate(root, query, criteriaBuilder));
	        predicates.add(hasSize(options.getSize()).toPredicate(root, query, criteriaBuilder));
	        predicates.add(hasGender(options.getGender()).toPredicate(root, query, criteriaBuilder));
	        predicates.add(hasSubCategory(options.getSubCategory()).toPredicate(root, query, criteriaBuilder));
	        predicates.add(hasCategory(options.getCategory()).toPredicate(root, query, criteriaBuilder));

	        // Trả về tập hợp các tiêu chí lọc
	        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	    };
	}
	
	
	// Lọc theo màu sắc (dựa trên quan hệ với ProductColorImg và Color)
	private static Specification<Product> hasColor(String color) {
	    return (root, query, criteriaBuilder) -> {
	        if (color == null || color.isEmpty()) {
	            return criteriaBuilder.conjunction(); // Không thêm điều kiện nếu màu sắc trống
	        }
	        Join<Product, ProductColorImg> productColorImgJoin = root.join("productColorImgs");
	        Join<ProductColorImg, Color> colorJoin = productColorImgJoin.join("color");
	        return criteriaBuilder.equal(colorJoin.get("name"), color);
	    };
	}

	// Lọc theo kích cỡ (dựa trên quan hệ với ProductSku và Size)
	private static Specification<Product> hasSize(String size) {
	    return (root, query, criteriaBuilder) -> {
	        if (size == null || size.isEmpty()) {
	            return criteriaBuilder.conjunction(); // Không thêm điều kiện nếu kích cỡ trống
	        }
	        Join<Product, ProductColorImg> productColorImgJoin = root.join("productColorImgs");
	        Join<ProductColorImg, ProductSku> productSkuJoin = productColorImgJoin.join("productSkus");
	        Join<ProductSku, Size> sizeJoin = productSkuJoin.join("size");
	        return criteriaBuilder.equal(sizeJoin.get("name"), size);
	    };
	}
	
	private static Specification<Product> hasGender(String gender){
		return (root , query , criteriaBuilder) -> {
				if(gender == null) {
					return criteriaBuilder.conjunction();
				}
				Join<Product, SubCategory> productSubCategoryJoin = root.join("subCategory");
				Join<SubCategory, Category> categoryJoin = productSubCategoryJoin.join("category");
				Join<Category, Gender> genderJoin = categoryJoin.join("gender");
				return criteriaBuilder.equal(genderJoin.get("name"), gender);
		};
	}
	
	private static Specification<Product> hasSubCategory(String subCategory){
		return (root , query , criteriaBuilder) -> {
				if(subCategory == null) {
					return criteriaBuilder.conjunction();
				}
				Join<Product, SubCategory> productSubCategoryJoin = root.join("subCategory");
				return criteriaBuilder.equal(productSubCategoryJoin.get("name"), subCategory);
		};
	}
	
	private static Specification<Product> hasCategory(String category){
		return (root , query , criteriaBuilder) -> {
				if(category == null) {
					return criteriaBuilder.conjunction();
				}
				Join<Product, SubCategory> productSubCategoryJoin = root.join("subCategory");
				Join<SubCategory, Category> categoryJoin = productSubCategoryJoin.join("category");
				return criteriaBuilder.equal(categoryJoin.get("name"), category);
		};
	}
}
