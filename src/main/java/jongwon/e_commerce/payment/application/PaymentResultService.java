package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayMethodMapper;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
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
        Pay payment = paymentRepository.findByOrderId(payOrderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        Order order = orderRepository.findByOrderId(payOrderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        //order 상태 변경
        order.markPaid();

        //payment 상태 변경
        payment.markSuccess();
        payment.setPayMethod(PayMethodMapper.from(method));
        payment.setApprovedAt(approvedAt);
    }

    public void applyFail(String payOrderId) {
        //결제 및 주문 조회
        Pay payment = paymentRepository.findByOrderId(payOrderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        Order order = orderRepository.findByOrderId(payOrderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        //payment 상태 변경
        payment.markFailed();

        //order 상태 변경
        order.markFailed();
    }

    public void applyTimeout(String payOrderId){
        // 결제 조회
        Pay payment = paymentRepository.findByOrderId(payOrderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        // payment 상태 변경
        payment.markSyncTimeout();
    }

}
