package xuandong.ecommerce_ver_2.service.product;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import xuandong.ecommerce_ver_2.dto.request.MultipleOptionsProductRequest;
import xuandong.ecommerce_ver_2.dto.request.admin.AddProductRequest;
import xuandong.ecommerce_ver_2.dto.request.admin.ProductSkus;
import xuandong.ecommerce_ver_2.dto.response.ProductDetailResponse;
import xuandong.ecommerce_ver_2.entity.Category;
import xuandong.ecommerce_ver_2.entity.Color;
import xuandong.ecommerce_ver_2.entity.Inventory;
import xuandong.ecommerce_ver_2.entity.Product;
import xuandong.ecommerce_ver_2.entity.ProductColorImg;
import xuandong.ecommerce_ver_2.entity.ProductSku;
import xuandong.ecommerce_ver_2.entity.Size;
import xuandong.ecommerce_ver_2.entity.SubCategory;
import xuandong.ecommerce_ver_2.exception.IdException;
import xuandong.ecommerce_ver_2.exception.StorageException;
import xuandong.ecommerce_ver_2.exception.UserAlreadyExistsException;
import xuandong.ecommerce_ver_2.repository.CategoryRepository;
import xuandong.ecommerce_ver_2.repository.ColorRepository;
import xuandong.ecommerce_ver_2.repository.GenderRepository;
import xuandong.ecommerce_ver_2.repository.InventoryRepository;
import xuandong.ecommerce_ver_2.repository.ProductColorImgRepository;
import xuandong.ecommerce_ver_2.repository.ProductRepository;
import xuandong.ecommerce_ver_2.repository.ProductSkuRepository;
import xuandong.ecommerce_ver_2.repository.SizeRepository;
import xuandong.ecommerce_ver_2.repository.SubCategoryRepository;
import xuandong.ecommerce_ver_2.service.file.FileService;
import xuandong.ecommerce_ver_2.utils.MultipleOptionsSpecificationBuilder;

@Service
public class ProductSkuService {
	private ProductSkuRepository productSkuRepository;
	private ProductRepository productRepository;
	private SubCategoryRepository subCategoryRepository;
	private InventoryRepository inventoryRepository;
	private GenderRepository genderRepository;
	private CategoryRepository categoryRepository;
	private FileService fileService;
	private ColorRepository colorRepository;
	private ProductColorImgRepository productColorImgRepository;
	private SizeRepository sizeRepository;

	public ProductSkuService(ProductSkuRepository productSkuRepository, ProductRepository productRepository,
			SubCategoryRepository subCategoryRepository, InventoryRepository inventoryRepository,
			GenderRepository genderRepository, CategoryRepository categoryRepository, FileService fileService,
			ColorRepository colorRepository, ProductColorImgRepository productColorImgRepository,
			SizeRepository sizeRepository) {
		this.productSkuRepository = productSkuRepository;
		this.productRepository = productRepository;
		this.subCategoryRepository = subCategoryRepository;
		this.inventoryRepository = inventoryRepository;
		this.genderRepository = genderRepository;
		this.categoryRepository = categoryRepository;
		this.fileService = fileService;
		this.colorRepository = colorRepository;
		this.productColorImgRepository = productColorImgRepository;
		this.sizeRepository = sizeRepository;
	}

//	public List<ProductDetailResponse> getProductSkues() {
//		List<ProductDetailResponse> responses = new ArrayList<>();
//		List<ProductSku> productSkues = productSkuRepository.findAll();
//		Map<Long, ProductDetailResponse> productResponseMap = new HashMap<>();
//
//		for (ProductSku productSku : productSkues) {
//			Long productId = productSku.getProductColorImg().getProduct().getId();
//			ProductDetailResponse productResponse = productResponseMap.getOrDefault(productId,
//					new ProductDetailResponse());
//
//			if (productResponse.getId() == null) {
//				// Initialize product details
//				productResponse.setId(productId);
//				productResponse.setName(productSku.getProductColorImg().getProduct().getName());
//				productResponse.setSubCategory(productSku.getProductColorImg().getProduct().getSubCategory().getName());
//				productResponse.setPrice(productSku.getPrice());
//				productResponse.setProductSkus(new ArrayList<>());
//				productResponseMap.put(productId, productResponse);
//			}
//
//			// Find if the same color already exists in the productSkus list
//			Optional<ProductDetailResponse.ProductSkuResponse> existingSku = productResponse.getProductSkus().stream()
//					.filter(sku -> sku.getColor().equals(productSku.getProductColorImg().getColor().getName())
//							&& sku.getImg().equals(productSku.getProductColorImg().getImage()))
//					.findFirst();
//
//			if (existingSku.isPresent()) {
//				// Add size to the existing SKU entry
//				existingSku.get().getSizes().add(productSku.getSize().getName());
//			} else {
//				// Create a new SKU response if color not found
//				ProductDetailResponse.ProductSkuResponse skuResponse = new ProductDetailResponse.ProductSkuResponse();
//				skuResponse.setColor(productSku.getProductColorImg().getColor().getName());
//				skuResponse.setImg(productSku.getProductColorImg().getImage());
//				skuResponse.setSizes(new ArrayList<>(Collections.singletonList(productSku.getSize().getName())));
//
//				// Add SKU to the list of SKUs in the product
//				productResponse.getProductSkus().add(skuResponse);
//			}
//		}
//
//		// Convert map values to list
//		responses.addAll(productResponseMap.values());
//
//		return responses;
//	}

	public ProductDetailResponse getSkusById(Long id) {
		List<ProductSku> productSkus = productSkuRepository.findByProductId(id);
		if (productSkus.isEmpty()) {
			throw new IdException("Product not found by id " + id);
		}

		Map<Long, ProductDetailResponse> productResponseMap = new HashMap<>();
		populateProductDetails(productResponseMap, productSkus);

		return productResponseMap.values().stream().findFirst().orElse(null);
	}

	public Page<ProductDetailResponse> getSkusBySubCategory(Long id, int currentPage, int pageSize,
			MultipleOptionsProductRequest options) {
		// Tạo đối tượng Pageable để xử lý phân trang
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

		// Lấy thực thể sub-category dựa trên ID
		SubCategory subCategory = subCategoryRepository.findById(id)
				.orElseThrow(() -> new IdException("Không tìm thấy danh mục con với ID " + id));

		Page<Product> productPage;

		// lọc với options
		if (options != null) {
			// set options
			String gender = subCategory.getCategory().getGender().getName();
			options.setGender(gender);
			options.setSubCategory(subCategory.getName());

			// Áp dụng lọc bằng Specification nếu có yêu cầu lọc
			Specification<Product> spec = MultipleOptionsSpecificationBuilder.hasMultipleOptions(options);
			productPage = productRepository.findAll(spec, pageable);
		} else {
			// Lấy danh sách sản phẩm theo danh mục con với phân trang
			productPage = productRepository.findBySubCategory(subCategory, pageable);
		}

		// Lấy danh sách ID của sản phẩm từ trang kết quả
		List<Long> productIds = productPage.stream().map(Product::getId).toList();

		// Lấy danh sách ProductSku theo danh sách ID sản phẩm
		List<ProductSku> productSkus = productSkuRepository.findByProductIds(productIds);

		List<ProductDetailResponse> responses = createProductDetailResponses(productSkus);

		// Chuyển đổi danh sách thành đối tượng Page với pageable gốc và tổng số phần tử
		return new PageImpl<>(responses, pageable, productPage.getTotalElements());
	}

	public Page<ProductDetailResponse> searchProduct(String search, int currentPage, int pageSize,
			MultipleOptionsProductRequest options) {
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

		// Bắt đầu với điều kiện tìm kiếm theo tên sản phẩm
		Specification<Product> spec = hasNameLike(search);

		// lọc với options
		if (options != null) {
			spec = MultipleOptionsSpecificationBuilder.hasMultipleOptions(options);
		}

		// Thực hiện truy vấn với các điều kiện lọc và phân trang
		Page<Product> productPage = productRepository.findAll(spec, pageable);

		// Lấy danh sách ID của sản phẩm từ trang kết quả
		List<Long> productIds = productPage.stream().map(Product::getId).toList();

		// Lấy danh sách ProductSku theo danh sách ID sản phẩm
		List<ProductSku> productSkus = productSkuRepository.findByProductIds(productIds);

		List<ProductDetailResponse> responses = createProductDetailResponses(productSkus);

		// Chuyển đổi danh sách thành đối tượng Page với pageable gốc và tổng số phần tử
		return new PageImpl<>(responses, pageable, productPage.getTotalElements());
	}

	public Page<ProductDetailResponse> getSkusByGender(String gender, int currentPage, int pageSize,
			MultipleOptionsProductRequest options) {
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

		Page<Product> productPage;

		// lọc với options
		if (options != null) {
			// Áp dụng lọc bằng Specification nếu có yêu cầu lọc
			Specification<Product> spec = MultipleOptionsSpecificationBuilder.hasMultipleOptions(options);
			productPage = productRepository.findAll(spec, pageable);
		} else {
			productPage = productRepository.findByGender(gender, pageable);
		}
		// Lấy danh sách ID của sản phẩm từ trang kết quả
		List<Long> productIds = productPage.stream().map(Product::getId).toList();

		// Lấy danh sách ProductSku theo danh sách ID sản phẩm
		List<ProductSku> productSkus = productSkuRepository.findByProductIds(productIds);

		List<ProductDetailResponse> responses = createProductDetailResponses(productSkus);

		// Chuyển đổi danh sách thành đối tượng Page với pageable gốc và tổng số phần tử
		return new PageImpl<>(responses, pageable, productPage.getTotalElements());
	}

	public Page<ProductDetailResponse> getSkusByCategory(String name, int currentPage, int pageSize,
			MultipleOptionsProductRequest options) {
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
		Page<Product> productPage;

		// lọc với options
		if (options != null) {
			Category category = categoryRepository.findByName(name)
					.orElseThrow(() -> new UsernameNotFoundException("Category not found"));

			// set options
			options.setCategory(category.getName());
			options.setGender(category.getGender().getName());

			Specification<Product> spec = MultipleOptionsSpecificationBuilder.hasMultipleOptions(options);
			productPage = productRepository.findAll(spec, pageable);
		} else {
			productPage = productRepository.findByCategory(name, pageable);
		}

		// Lấy danh sách ID của sản phẩm từ trang kết quả
		List<Long> productIds = productPage.stream().map(Product::getId).toList();

		// Lấy danh sách ProductSku theo danh sách ID sản phẩm
		List<ProductSku> productSkus = productSkuRepository.findByProductIds(productIds);

		List<ProductDetailResponse> responses = createProductDetailResponses(productSkus);

		// Chuyển đổi danh sách thành đối tượng Page với pageable gốc và tổng số phần tử
		return new PageImpl<>(responses, pageable, productPage.getTotalElements());

	}

	@Transactional
	public void addProduct(AddProductRequest addProductRequest, MultipartFile file)
			throws URISyntaxException, IOException {
		if (file == null || file.isEmpty()) {
			throw new StorageException("File is empty. Please upload a file.");
		}

		// Kiểm tra định dạng file
		String fileName = file.getOriginalFilename();
		List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png");
		String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
		if (!allowedExtensions.contains(fileExtension)) {
			throw new StorageException("Invalid file extension. Only allows " + allowedExtensions.toString());
		}

		// Lưu hình ảnh
		String pathImage = fileService.store(file);

		// Tìm và kiểm tra SubCategory
		SubCategory subCategory = subCategoryRepository.findByName(addProductRequest.getNameSubCategory())
				.orElseThrow(() -> new UsernameNotFoundException("Sub Category not found"));

		// Kiểm tra sản phẩm đã tồn tại
		Optional<Product> existingProduct = productRepository.findByName(addProductRequest.getNameProduct());
		if (existingProduct.isPresent()) {
			throw new UserAlreadyExistsException("Tên product này đã tồn tại: " + addProductRequest.getNameProduct());
		}

		// Tạo mới sản phẩm
		Product product = new Product();
		product.setName(addProductRequest.getNameProduct());
		product.setDescription(addProductRequest.getDescription());
		product.setSubCategory(subCategory);
		productRepository.save(product);

		// Tạo mới ProductColorImg
		Color color = colorRepository.findByName(addProductRequest.getColor())
				.orElseThrow(() -> new UsernameNotFoundException("Color not found"));
		ProductColorImg productColorImg = new ProductColorImg();
		productColorImg.setProduct(product);
		productColorImg.setImage(pathImage);
		productColorImg.setColor(color);
		productColorImgRepository.save(productColorImg);

		// Tạo mới SKU và kho hàng
		List<ProductSkus> productSkus = addProductRequest.getProductSkus();
		List<ProductSku> skusToSave = new ArrayList<>();
		List<Inventory> inventoriesToSave = new ArrayList<>();

		for (ProductSkus productSku : productSkus) {
			Size size = sizeRepository.findByName(productSku.getSize())
					.orElseThrow(() -> new UsernameNotFoundException("Size not found"));

			ProductSku sku = new ProductSku();
			sku.setProductColorImg(productColorImg);
			sku.setPrice(productSku.getPrice());
			sku.setSize(size);
			skusToSave.add(sku);

			// Kho hàng
			Inventory inventory = new Inventory();
			inventory.setProductSkus(sku);
			inventory.setStock(productSku.getStock());
			inventoriesToSave.add(inventory);
		}

		// Lưu SKU và kho hàng
		productSkuRepository.saveAll(skusToSave);
		inventoryRepository.saveAll(inventoriesToSave);
	}

	// xóa product gốc
	public void deleteProduct(String name) {
		Product product = productRepository.findByName(name)
				.orElseThrow(() -> new UsernameNotFoundException("Product not found"));
		product.setDelete(true);
		productRepository.save(product);
	}
	
	public void deleteProductImageAndColor(Long ProductId , String nameColor) {
		Color color = colorRepository.findByName(nameColor).orElseThrow(() -> new UsernameNotFoundException("Color not found"));
		Product product =productRepository.findById(ProductId).orElseThrow(() -> new UsernameNotFoundException("Product not found"));
		ProductColorImg productColorImg = productColorImgRepository.findByProductAndColor(product, color).orElseThrow(() -> new UsernameNotFoundException("ProductColorImg not found"));
		productColorImg.setDelete(true);
		productColorImgRepository.save(productColorImg);
		
	}
	
	 // Điền chi tiết sản phẩm 
	private void populateProductDetails(Map<Long, ProductDetailResponse> productResponseMap,
			List<ProductSku> productSkus) {
		for (ProductSku productSku : productSkus) {
			Long productId = productSku.getProductColorImg().getProduct().getId();
			ProductDetailResponse productResponse = productResponseMap.getOrDefault(productId,
					new ProductDetailResponse());

			if (productResponse.getId() == null) {
				productResponse.setId(productId);
				productResponse.setName(productSku.getProductColorImg().getProduct().getName());
				productResponse.setSubCategory(productSku.getProductColorImg().getProduct().getSubCategory().getName());
				productResponse.setPrice(productSku.getPrice());
				productResponse.setProductSkus(new ArrayList<>());
				productResponseMap.put(productId, productResponse);
			}

			processSku(productSku, productResponse);
		}
	}
	
	

	private void processSku(ProductSku productSku, ProductDetailResponse productResponse) {
		// Tìm kiếm ProductSkuResponse hiện tại dựa trên màu sắc và hình ảnh
		Optional<ProductDetailResponse.ProductSkuResponse> existingSku = productResponse.getProductSkus().stream()
				.filter(sku -> sku.getColor().equals(productSku.getProductColorImg().getColor().getName())
						&& sku.getImg().equals(productSku.getProductColorImg().getImage()))
				.findFirst();

		// Lấy số lượng tồn kho từ InventoryRepository
		Inventory inventory = inventoryRepository.findByProductSkus(productSku)
				.orElseThrow(() -> new IdException("Not found inventory by id " + productSku.getId()));
		Integer stock = inventory.getStock();

		if (existingSku.isPresent()) {
			// Thêm hoặc cập nhật size và stock trong entry SKU hiện tại
			String size = productSku.getSize().getName();
			Integer currentStock = existingSku.get().getSizeAndStock().getOrDefault(size, 0);
			existingSku.get().getSizeAndStock().put(size, currentStock + stock);
		} else {
			// Tạo mới một SKU response nếu không tìm thấy màu sắc tương ứng
			ProductDetailResponse.ProductSkuResponse skuResponse = new ProductDetailResponse.ProductSkuResponse();
			skuResponse.setSkuId(productSku.getProductColorImg().getId());
			skuResponse.setColor(productSku.getProductColorImg().getColor().getName());
			skuResponse.setImg(productSku.getProductColorImg().getImage());

			// Khởi tạo map sizeAndStock với size hiện tại và số lượng tồn kho
			Map<String, Integer> sizeAndStock = new HashMap<>();
			sizeAndStock.put(productSku.getSize().getName(), stock);
			skuResponse.setSizeAndStock(sizeAndStock);

			// Thêm SKU mới vào danh sách SKUs của product
			productResponse.getProductSkus().add(skuResponse);
		}
	}

	 // Lấy chi tiết sản phẩm từ ProductSku và ánh xạ vào ProductDetailResponse
    private List<ProductDetailResponse> createProductDetailResponses(List<ProductSku> productSkus) {
        // Tạo bản đồ để ánh xạ chi tiết sản phẩm
        Map<Long, ProductDetailResponse> productResponseMap = new HashMap<>();
        populateProductDetails(productResponseMap, productSkus);

        // Trả về danh sách chi tiết sản phẩm
        return new ArrayList<>(productResponseMap.values());
    }

	private Specification<Product> hasNameLike(String keywords) {
		return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
			// Tách chuỗi keywords thành các từ bằng dấu cách
			String[] keywordArray = keywords.split("\\s+");

			// Danh sách các điều kiện LIKE
			List<Predicate> predicates = new ArrayList<>();

			// Tạo LIKE cho từng từ khóa và thêm vào danh sách predicates
			for (String keyword : keywordArray) {
				predicates.add(criteriaBuilder.like(root.get("name"), "%" + keyword + "%"));
			}

			// Kết hợp các điều kiện lại bằng toán tử OR
			return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
		};
	}

}
