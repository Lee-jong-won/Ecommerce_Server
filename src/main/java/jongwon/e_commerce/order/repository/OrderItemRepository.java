package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import org.aspectj.weaver.ast.Or;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);

    List<OrderItem> findByOrder(Order order);

    Optional<OrderItem> findById(Long id);

    void clearStore();
}
