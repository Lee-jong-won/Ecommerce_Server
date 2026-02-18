package jongwon.e_commerce.product.repository;

import jongwon.e_commerce.product.domain.Product;
import java.util.*;

public class ProductMemoryRepository implements ProductRepository {

    private static Map<Long, Product> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Product save(String productName, int productPrice) {
        Product product = Product.create(productName, productPrice);
        store.put(++sequence, product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void clearStore(){
        store.clear();;
    }
}
