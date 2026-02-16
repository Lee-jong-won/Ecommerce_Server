package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.product.repository.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public void decreaseStock(String payOrderId) {
        Order order = orderRepository.findByPayOrderId(payOrderId);
        List<OrderItem> orderItems = orderItemRepository.findOrderItems(order.getOrderId());
        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findById(orderItem.getProductId());
            product.removeStock(orderItem.getOrderQuantity());
        }
    }

    public void increaseStock(String payOrderId){
        Order order = orderRepository.findByPayOrderId(payOrderId);
        List<OrderItem> orderItems = orderItemRepository.findOrderItems(order.getOrderId());
        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findById(orderItem.getProductId());
            product.addStock(orderItem.getOrderQuantity());
        }
    }

}
