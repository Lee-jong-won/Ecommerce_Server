package jongwon.e_commerce.payment.application.approve.validate;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderValidator {

    private final OrderRepository orderRepository;

    @Transactional
    public Order validateForPayment(String orderId, long amount) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException()
        );
        //가격이 일치 하지 않으면, 예외 throw
        if (order.getTotalAmount() != amount)
            throw new InvalidAmountException();
        return order;
    }
}
