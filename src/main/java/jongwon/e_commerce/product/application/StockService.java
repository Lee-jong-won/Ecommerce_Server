package jongwon.e_commerce.product.application;

import jongwon.e_commerce.product.domain.Product;

public interface StockService {
    boolean support(Product product);
    Product increaseStock(Product product, int quantity);
    Product decreaseStock(Product product, int quantity);
}
