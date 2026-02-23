package jongwon.e_commerce.payment.application.approve.process3.success;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockChanger {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public void decreaseStock(String orderId) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        List<OrderItem> orderItems = orderItemRepository.findOrderItems(order.getId());
        for (OrderItem orderItem : orderItems) {

            Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(
                    () -> new ProductNotFoundException(orderItem.getProductId())
            );

            product.removeStock(orderItem.getOrderQuantity());
        }
    }

    public void increaseStock(String orderId){

        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        List<OrderItem> orderItems = orderItemRepository.findOrderItems(order.getId());

        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(
                    () -> new ProductNotFoundException(orderItem.getProductId())
            );

            product.addStock(orderItem.getOrderQuantity());
        }
    }

}
