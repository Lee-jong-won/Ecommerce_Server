package jongwon.e_commerce.payment.application.approve.process3.common;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethodMapper;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PaymentStateUpdater {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public void applySuccess(String orderId, OffsetDateTime approvedAt, String method) {
        //결제 및 주문 조회
        Pay payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        // order 상태 변경
        order.markPaid();

        // pay 상태 변경
        payment.markSuccess();

        // pay 상태 변경
        payment.setApprovedAt(approvedAt);
        payment.setPayMethod(PayMethodMapper.from(method));
    }

    public void applyFail(String orderId) {
        //결제 및 주문 조회
        Pay payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        // payment 상태 변경
        payment.markFailed();

        //order 상태 변경
        order.markFailed();
    }

    public void applyTimeout(String orderId){
        // 결제 조회
        Pay payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        // payment 상태 변경
        payment.markSyncTimeout();
    }

}
