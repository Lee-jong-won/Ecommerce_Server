package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.order.repository.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Primary
@Repository
public class OrderJpaRepositoryImpl implements OrderRepository {

    @Autowired
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        return orderJpaRepository.save(orderEntity);
    }
    @Override
    public Optional<OrderEntity> findById(Long id) {
        return orderJpaRepository.findById(id);
    }
    @Override
    public Optional<OrderEntity> findByOrderId(String orderId) {
        return orderJpaRepository.findByOrderId(orderId);
    }
    @Override
    public void clearStore() {
        orderJpaRepository.deleteAll();
    }

}
