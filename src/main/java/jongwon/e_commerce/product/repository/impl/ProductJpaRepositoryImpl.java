package jongwon.e_commerce.product.repository.impl;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.jpa.ProductJpaRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class ProductJpaRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public void clearStore() {
        productJpaRepository.deleteAll();
    }
}
