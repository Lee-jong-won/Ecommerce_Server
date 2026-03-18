package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.Order;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Order getById(Long id);
    Order getByOrderId(String orderId);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderId(String orderId);
}
