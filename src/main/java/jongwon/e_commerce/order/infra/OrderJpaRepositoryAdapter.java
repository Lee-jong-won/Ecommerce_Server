package jongwon.e_commerce.order.infra;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
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
    public Order findById(Long id) {
        return orderJpaRepository.findById(id).orElseThrow(
                () -> new OrderNotExistException("해당 주문은 존재하지 않습니다.")
        );
    }
}
