package com.portfolio.ecommerce.repository;

import com.portfolio.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            WHERE (COALESCE(:search, '') = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(p.category) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (COALESCE(:category, '') = '' OR LOWER(p.category) = LOWER(:category))
            ORDER BY p.name
            """)
    List<Product> search(@Param("search") String search, @Param("category") String category);

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findDistinctCategories();
}
