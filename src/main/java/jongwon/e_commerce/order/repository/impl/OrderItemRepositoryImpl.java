package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;
    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemJpaRepository.save(OrderItemEntity.from(orderItem)).toModel();
    }

    @Override
    public List<OrderItem> findByOrder(Order order) {
        List<OrderItemEntity> orderItemEntities = orderItemJpaRepository.findOrderItemsBy(OrderEntity.from(order));
        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemEntity orderItemEntity : orderItemEntities){
            OrderItem orderItem = orderItemEntity.toModel();
            orderItems.add(orderItem);
        }
        return orderItems;
    }
}
