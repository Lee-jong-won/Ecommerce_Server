package jongwon.e_commerce.product.application;

import jongwon.e_commerce.product.domain.Product;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class HotItemStockService implements StockService{
    @Override
    public boolean support(Product product) {
        return product.getOriginalProductId() != null;
    }

    @Override
    public Product increaseStock(Product product, int quantity) {
        return null;
    }

    @Override
    public Product decreaseStock(Product product, int quantity) {
        return null;
    }
}
