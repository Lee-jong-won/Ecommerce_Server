package jongwon.e_commerce.order.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Builder
public class OrderStockProcessor {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public List<Product> deductStockOf(Order order) {
        List<Product> products = new ArrayList<>();
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.removeStock(orderItem.getOrderQuantity());
            products.add(productRepository.save(product));
        }
        return products;
    }

    public List<Product> restoreStockOf(Order order){
        List<Product> products = new ArrayList<>();
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.addStock(orderItem.getOrderQuantity());
            products.add(productRepository.save(product));
        }
        return products;
    }
}
