package jongwon.e_commerce.product.repository;

import jongwon.e_commerce.product.domain.Product;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    Product getById(Long id);
    int decreaseStock(Long id, int quantity, Long version);
    int increaseStock(Long id, int quantity, Long version);
}
