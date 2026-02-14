package jongwon.e_commerce.product.repository;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.infra.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@RequiredArgsConstructor
public class ProductJpaRepositoryAdapter implements ProductRepository{

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(String productName, int productPrice) {
        Product product = Product.create(productName, productPrice);
        return productJpaRepository.save(product);
    }

    @Override
    public Product findById(Long id) {
        return productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
