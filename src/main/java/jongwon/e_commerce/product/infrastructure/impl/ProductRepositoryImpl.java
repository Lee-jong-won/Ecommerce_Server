package jongwon.e_commerce.product.infrastructure.impl;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.infrastructure.jpa.ProductEntity;
import jongwon.e_commerce.product.infrastructure.jpa.ProductJpaRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    @Override
    public Product save(Product product) {
        return productJpaRepository.saveAndFlush(ProductEntity.from(product)).toModel();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(ProductEntity::toModel);
    }

    @Override
    public Product getById(Long id) {
        return findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", id)
        );
    }

    @Override
    public List<Product> findAllById(Iterable<Long> iterable) {
        return productJpaRepository.findAllById(iterable).stream().map(ProductEntity::toModel).toList();
    }

}
