package com.portfolio.ecommerce.service;

import com.portfolio.ecommerce.dto.AddCartItemRequest;
import com.portfolio.ecommerce.dto.CartItemResponse;
import com.portfolio.ecommerce.dto.CartResponse;
import com.portfolio.ecommerce.dto.UpdateCartItemRequest;
import com.portfolio.ecommerce.entity.Cart;
import com.portfolio.ecommerce.entity.CartItem;
import com.portfolio.ecommerce.entity.Product;
import com.portfolio.ecommerce.exception.BadRequestException;
import com.portfolio.ecommerce.exception.ResourceNotFoundException;
import com.portfolio.ecommerce.repository.CartItemRepository;
import com.portfolio.ecommerce.repository.CartRepository;
import com.portfolio.ecommerce.repository.ProductRepository;
import com.portfolio.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(UserRepository userRepository,
                       CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public CartResponse getCart(Long userId) {
        return toResponse(findCart(userId));
    }

    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        Cart cart = findCart(userId);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + request.productId()));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
        int nextQuantity = request.quantity();
        if (item != null) {
            nextQuantity = item.getQuantity() + request.quantity();
        }
        ensureStock(product, nextQuantity);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.quantity());
            cart.getItems().add(item);
        } else {
            item.setQuantity(nextQuantity);
        }
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        CartItem item = findItem(userId, itemId);
        ensureStock(item.getProduct(), request.quantity());
        item.setQuantity(request.quantity());
        cartItemRepository.save(item);
        return toResponse(item.getCart());
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        CartItem item = findItem(userId, itemId);
        Cart cart = findCart(userId);
        // orphanRemoval on Cart.items deletes the row when it leaves the collection.
        cart.getItems().removeIf(existing -> existing.getId().equals(item.getId()));
        cartRepository.save(cart);
        return toResponse(cart);
    }

    private Cart findCart(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }
        return cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user " + userId));
    }

    private CartItem findItem(Long userId, Long itemId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }
        return cartItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id " + itemId));
    }

    private void ensureStock(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException(
                    "Only " + product.getStockQuantity() + " of " + product.getName() + " left in stock");
        }
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .sorted(Comparator.comparing(CartItem::getId, Comparator.nullsLast(Long::compareTo)))
                .map(this::toItemResponse)
                .toList();
        BigDecimal total = items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), cart.getUser().getId(), total, items);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal unitPrice = item.getProduct().getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                unitPrice,
                item.getQuantity(),
                item.getProduct().getStockQuantity(),
                lineTotal
        );
    }
}
