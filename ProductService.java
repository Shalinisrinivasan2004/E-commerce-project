package com.portfolio.ecommerce.service;

import com.portfolio.ecommerce.dto.ProductRequest;
import com.portfolio.ecommerce.dto.ProductResponse;
import com.portfolio.ecommerce.entity.Product;
import com.portfolio.ecommerce.exception.ConflictException;
import com.portfolio.ecommerce.exception.ResourceNotFoundException;
import com.portfolio.ecommerce.repository.CartItemRepository;
import com.portfolio.ecommerce.repository.OrderItemRepository;
import com.portfolio.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository,
                          CartItemRepository cartItemRepository,
                          OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return toResponse(productRepository.save(product));
    }

    public List<ProductResponse> findAll(String search, String category) {
        return productRepository.search(blankToNull(search), blankToNull(category))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<String> categories() {
        return productRepository.findDistinctCategories();
    }

    public ProductResponse getById(Long id) {
        return toResponse(findProduct(id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        apply(product, request);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = findProduct(id);
        if (orderItemRepository.existsByProduct_Id(id)) {
            throw new ConflictException("This product is part of an existing order and cannot be deleted");
        }
        cartItemRepository.deleteByProductId(id);
        productRepository.delete(product);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price().setScale(2, RoundingMode.HALF_UP));
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(request.category().trim());
        product.setImageUrl(blankToNull(request.imageUrl()));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory(),
                product.getImageUrl(),
                product.getCreatedAt()
        );
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
