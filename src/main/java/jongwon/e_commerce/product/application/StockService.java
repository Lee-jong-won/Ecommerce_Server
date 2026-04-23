package jongwon.e_commerce.product.application;

import jongwon.e_commerce.product.domain.Product;

public interface StockService {
    boolean support(Product product);
    Product increaseStock(Long productId, int quantity);
    Product decreaseStock(Long productId, int quantity);
}
