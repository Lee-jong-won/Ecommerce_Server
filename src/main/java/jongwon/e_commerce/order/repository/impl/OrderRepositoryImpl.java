package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.order.repository.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Primary
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    private final OrderJpaRepository orderJpaRepository;
    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(OrderEntity.from(order)).toModel();
    }

    @Override
    public Order getById(Long id) {
        return findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Order", id)
        );
    }

    @Override
    public Order getByOrderId(String orderId) {
        return findByOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order", orderId)
        );
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id).map(OrderEntity::toModel);
    }
    @Override
    public Optional<Order> findByOrderId(String orderId) {
        return orderJpaRepository.findByOrderId(orderId).map(OrderEntity::toModel);
    }

}
