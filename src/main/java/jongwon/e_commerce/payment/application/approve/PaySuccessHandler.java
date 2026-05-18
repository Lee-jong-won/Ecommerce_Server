package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaySuccessHandler {

    private final OrderStockProcessor orderStockProcessor;
    private final OrderRepository orderRepository;
    private final PayRequestRepository payRequestRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void process(Long payRequestId, PayResult result) {
        // 결제 요청 상태 변경
        PayRequest payRequest = payRequestRepository.getById(payRequestId);
        payRequest.complete();
        payRequestRepository.save(payRequest);

        // 결제 결과 영속화
        Pay pay = Pay.from(result.getPayResultCommon(), result.getPaymentDetail(), payRequestId);
        paymentRepository.save(pay);

        // 주문 상태 변경
        Order order = orderRepository.getById(payRequest.getOrderId());
        order.paid();
        orderRepository.save(order);

        // 재고 감소
        orderStockProcessor.deductStockOf(order);
    }

}
