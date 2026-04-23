package jongwon.e_commerce.product.application;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
public class GeneralItemStockService implements StockService{

    private final GeneralItemStockServiceTx generalItemStockServiceTx;
    @Qualifier("stockRetryTemplate") private final RetryOperations retryOperations;

    public GeneralItemStockService(GeneralItemStockServiceTx generalItemStockServiceTx,
                                   @Qualifier("stockRetryTemplate")RetryOperations retryOperations){
        this.retryOperations = retryOperations;
        this.generalItemStockServiceTx = generalItemStockServiceTx;
    }

    @Override
    public boolean support(Product product) {
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

/*
    public Product decreaseStock(Long productId, int quantity) {
        return retryOperations.execute(execution -> {
            return decreaseStockTx(productId, quantity);
        });
    }

    public Product increaseStock(Long productId, int quantity) {
        return retryOperations.execute(execution -> {
            return increaseStockTx(productId, quantity);
        });
    }
*/

    /*@Transactional(propagation = Propagation.REQUIRES_NEW)
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
    }*/
}
