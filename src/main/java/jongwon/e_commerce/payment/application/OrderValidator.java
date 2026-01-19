package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {
    private final OrderRepository orderRepository;

    public OrderValidator(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order validateOrder(TossPaymentApproveRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new OrderNotExistException("결제 정보에 대응되는 주문 정보가 존재하지 않습니다.")
                );

        validateAmount(order, request);
        return order;
    }

    private void validateAmount(Order order, TossPaymentApproveRequest request) {
        if (order.getTotalAmount() != request.getAmount()) {
            throw new InvalidAmountException("주문 내역의 금액과 결제 금액이 일치하지 않습니다.");
        }
    }
}
