package jongwon.e_commerce.product.application;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// @Service
@Builder
public class GeneralItemStockService implements StockService{

    private final GeneralItemStockServiceTx generalItemStockServiceTx;
    private final ProductRepository productRepository;
    @Qualifier("stockRetryTemplate") private final RetryOperations retryOperations;

    public GeneralItemStockService(GeneralItemStockServiceTx generalItemStockServiceTx,
                                   ProductRepository productRepository,
                                   @Qualifier("stockRetryTemplate")RetryOperations retryOperations){
        this.retryOperations = retryOperations;
        this.productRepository = productRepository;
        this.generalItemStockServiceTx = generalItemStockServiceTx;
    }

    @Override
    public boolean support(Long productId) {
        Product product = productRepository.getById(productId);
        return product.getOriginalProductId() == null;
    }

    @Transactional
    public Product decreaseStock(Long productId, int quantity) {
        return retryOperations.execute(execution -> {
            return generalItemStockServiceTx.decreaseStockTx(productId, quantity);
        });
    }

    @Transactional
    public Product increaseStock(Long productId, int quantity) {
        return retryOperations.execute(execution -> {
            return generalItemStockServiceTx.increaseStockTx(productId, quantity);
        });
    }

}
