package jongwon.e_commerce.product.application;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Builder
public class GeneralItemStockService implements StockService{

    private final ProductRepository productRepository;

    @Override
    public boolean support(Product product) {
        return product.getOriginalProductId() == null;
    }

    @Transactional
    public Product decreaseStock(Product product, int quantity){
        product.removeStock(quantity);
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;
    }

    @Transactional
    public Product increaseStock(Product product, int quantity){
        product.addStock(quantity);
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;
    }

}
