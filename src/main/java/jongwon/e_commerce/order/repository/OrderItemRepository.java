package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.OrderItem;
import org.aspectj.weaver.ast.Or;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {
    OrderItem save(Long orderId, Long productId,
                   String productName, int orderPrice, int orderQuantity);

    List<OrderItem> findOrderItems(Long orderId);

    Optional<OrderItem> findById(Long id);

}
