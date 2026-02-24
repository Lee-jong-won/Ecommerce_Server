package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class OrderItemJpaRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;
    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> findByOrder(Order order) {
        List<OrderItem> orderItems = orderItemJpaRepository.findOrderItemsBy(order);
        return orderItems;
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        return orderItemJpaRepository.findById(id);
    }

    @Override
    public void clearStore() {
        orderItemJpaRepository.deleteAll();
    }
}
