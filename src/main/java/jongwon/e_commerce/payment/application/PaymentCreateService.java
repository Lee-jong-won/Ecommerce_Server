package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class PaymentCreateService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    public Pay preparePayment(String orderId){
        // 1. OrderId(String)로 주문 조회
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        // 2. Payment 생성
        Pay pay = paymentRepository.save( order.getId(),
                order.getOrderId(),
                order.getTotalAmount());
        return pay;
    }
}
