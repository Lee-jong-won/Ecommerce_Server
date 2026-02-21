package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethodMapper;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentResultUpdaterImpl implements PaymentResultUpdater {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public void applySuccess(String orderId, OffsetDateTime approvedAt, String method) {
        //결제 및 주문 조회
        Pay payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        //order 상태 변경
        order.markPaid();

        //payment 상태 변경
        payment.markSuccess();
        payment.setPayMethod(PayMethodMapper.from(method));
        payment.setApprovedAt(approvedAt);
    }

    @Override
    public void applyFail(String orderId) {
        //결제 및 주문 조회
        Pay payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
        );

        //payment 상태 변경
        payment.markFailed();

        //order 상태 변경
        order.markFailed();
    }

    @Override
    public void applyTimeout(String orderId){
        // 결제 조회
        Pay payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        // payment 상태 변경
        payment.markSyncTimeout();
    }

}
