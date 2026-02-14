package jongwon.e_commerce.product.repository;

import jongwon.e_commerce.product.domain.Product;

import java.util.Optional;

public interface ProductRepository {
    Product save(String productName, int productPrice);
    Product findById(Long id);
}
