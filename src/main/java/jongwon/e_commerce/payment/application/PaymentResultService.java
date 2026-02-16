package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentResultService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public void applySuccess(String payOrderId, OffsetDateTime approvedAt, String method) {
        //결제 및 주문 조회
        Pay payment = paymentRepository.findByOrderId(payOrderId);
        Order order = orderRepository.findByPayOrderId(payOrderId);

        //order 상태 변경
        order.markPaid();

        //payment 상태 변경
        payment.markSuccess();
        payment.setPayMethod(PayMethod.valueOf(method));
        payment.setApprovedAt(approvedAt);
    }

    public void applyFail(String payOrderId) {

        //결제 및 주문 조회
        Pay payment = paymentRepository.findByOrderId(payOrderId);
        Order order = orderRepository.findByPayOrderId(payOrderId);

        //payment 상태 변경
        payment.markFailed();

        //order 상태 변경
        order.markFailed();
    }

    public void applyTimeout(String payOrderId){
        // 결제 조회
        Pay payment = paymentRepository.findByOrderId(payOrderId);

        // payment 상태 변경
        payment.markSyncTimeout();
    }

}
