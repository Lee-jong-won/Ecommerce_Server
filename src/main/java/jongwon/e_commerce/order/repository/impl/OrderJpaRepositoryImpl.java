package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.order.domain.Order;
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
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }
    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }
    @Override
    public Optional<Order> findByOrderId(String orderId) {
        return orderJpaRepository.findByOrderId(orderId);
    }
    @Override
    public void clearStore() {
        orderJpaRepository.deleteAll();
    }

}
