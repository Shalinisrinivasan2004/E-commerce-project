package com.portfolio.ecommerce.repository;

import com.portfolio.ecommerce.entity.CustomerOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    List<CustomerOrder> findByUser_IdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Optional<CustomerOrder> findWithItemsById(Long id);

    boolean existsByUser_Id(Long userId);
}
