package jongwon.e_commerce.product.repository.impl;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;

import java.util.*;

public class ProductMemoryRepository implements ProductRepository {

    private static Map<Long, Product> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Product save(Product product) {
        product.setProductId(++sequence);
        store.put(product.getProductId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void clearStore(){
        store.clear();;
    }
}
