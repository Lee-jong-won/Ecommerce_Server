package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
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
    public void preparePayment(String orderId, String pgType){

        // 1. OrderId(String)로 주문 조회
        Order order = orderRepository.findByPayOrderId(orderId);

        // 2. Payment 생성
        paymentRepository.save( order.getOrderId(),
                order.getPayOrderId(),
                pgType,
                order.getTotalAmount());
    }
}
