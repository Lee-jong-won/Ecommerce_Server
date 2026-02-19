package jongwon.e_commerce.order.repository.adapter;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.order.repository.jpa.OrderJpaRepository;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Primary
@Repository
public class OrderJpaRepositoryAdapter implements OrderRepository {

    @Autowired
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Long memberId, String orderName) {
        Order order = Order.createOrder(memberId, orderName);
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public Optional<Order> findByOrderId(String payOrderId) {
        return orderJpaRepository.findByPayOrderId(payOrderId);
    }

}
