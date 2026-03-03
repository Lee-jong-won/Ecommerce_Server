package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;
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
    public OrderItemEntity save(OrderItemEntity orderItemEntity) {
        return orderItemJpaRepository.save(orderItemEntity);
    }

    @Override
    public List<OrderItemEntity> findByOrder(OrderEntity orderEntity) {
        List<OrderItemEntity> orderItemEntities = orderItemJpaRepository.findOrderItemsBy(orderEntity);
        return orderItemEntities;
    }

    @Override
    public Optional<OrderItemEntity> findById(Long id) {
        return orderItemJpaRepository.findById(id);
    }

    @Override
    public void clearStore() {
        orderItemJpaRepository.deleteAll();
    }
}
