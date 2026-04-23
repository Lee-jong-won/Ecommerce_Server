package jongwon.e_commerce.product.repository;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;

import java.util.Optional;

public interface ProductStockRepository {
    Optional<Product> findById(Long productId);
    Product getById(Long productId);
    int updateStockAndStatus(Long productId, int stockQuantity, ProductStatus productStatus, Integer version);
}
