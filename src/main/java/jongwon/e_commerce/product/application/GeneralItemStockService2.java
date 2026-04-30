package jongwon.e_commerce.product.application;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.product.repository.ProductStockRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
public class GeneralItemStockService2 implements StockService{

    private final ProductStockRepository productStockRepository;
    @Qualifier("stockRetryTemplate") private final RetryOperations retryOperations;

    public GeneralItemStockService2(ProductStockRepository productStockRepository,
                                    @Qualifier("stockRetryTemplate")RetryOperations retryOperations){
        this.retryOperations = retryOperations;
        this.productStockRepository = productStockRepository;
    }

    @Override
    public boolean support(Product product) {
        return product.getOriginalProductId() == null;
    }

    @Transactional
    public Product decreaseStock(Long productId, int quantity) {
        return retryOperations.execute(execution -> {
            Product product = productStockRepository.getById(productId);
            product.removeStock(quantity);

            int success = productStockRepository.updateStockAndStatus(
                    product.getProductId(),
                    product.getStockQuantity(),
                    product.getProductStatus(),
                    product.getVersion());

            if(success == 0)
                throw new ObjectOptimisticLockingFailureException(Product.class, product.getProductId());

            return product;
        });
    }

    @Transactional
    public Product increaseStock(Long productId, int quantity) {
        return retryOperations.execute(execution -> {
            Product product = productStockRepository.getById(productId);
            product.addStock(quantity);

            int success = productStockRepository.updateStockAndStatus(
                    product.getProductId(),
                    product.getStockQuantity(),
                    product.getProductStatus(),
                    product.getVersion());

            if(success == 0)
                throw new ObjectOptimisticLockingFailureException(Product.class, product.getProductId());

            return product;
        });
    }
}
