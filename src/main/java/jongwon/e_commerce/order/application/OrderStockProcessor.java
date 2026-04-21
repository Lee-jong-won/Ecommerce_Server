package jongwon.e_commerce.order.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.product.application.StockService;
import jongwon.e_commerce.product.domain.Product;
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
    private final List<StockService> stockServices;

    public List<Product> deductStockOf(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<Product> deductedProducts = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            stockServices.stream()
                    .filter(h -> h.support(orderItem.getProduct()))
                    .findAny()
                    .ifPresent(h -> {
                        Product deductedProduct = h.decreaseStock(orderItem.getProduct(), orderItem.getOrderQuantity());
                        deductedProducts.add(deductedProduct);
                    });
        }

        return deductedProducts;
    }

    public List<Product> restoreStockOf(Order order){
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<Product> increasedProducts = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            stockServices.stream()
                    .filter(h -> h.support(orderItem.getProduct()))
                    .findAny()
                    .ifPresent(h -> {
                        Product increasedProduct = h.increaseStock(orderItem.getProduct(), orderItem.getOrderQuantity());
                        increasedProducts.add(increasedProduct);
                    });
        }

        return increasedProducts;
    }
}
