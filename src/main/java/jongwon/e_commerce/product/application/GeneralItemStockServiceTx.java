package jongwon.e_commerce.product.application;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Builder
@Service
public class GeneralItemStockServiceTx {

    private final ProductRepository productRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product decreaseStockTx(Long productId, int quantity) {
        Product product = productRepository.getById(productId);
        product.removeStock(quantity);
        return productRepository.save(product);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product increaseStockTx(Long productId, int quantity) {
        Product product = productRepository.getById(productId);
        product.addStock(quantity);
        return productRepository.save(product);
    }
}
