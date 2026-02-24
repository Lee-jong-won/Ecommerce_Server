package jongwon.e_commerce.payment.application.approve.result.persistence;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentContext;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentDetailCreator;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCreator {

    private final List<PaymentDetailCreator> detailCreators;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createPayment(PaymentContext paymentContext){
        // 1. 결제 공통 정보 저장
        Order order = orderRepository.findByOrderId(paymentContext.getOrderId())
                .orElseThrow(
                        () -> new OrderNotExistException("해당 id를 갖는 주문이 존재하지 않습니다")
                );
        Pay pay = Pay.from(paymentContext);
        pay.setOrder(order);
        paymentRepository.save(pay);

        // 2. 결제 수단별 상세 정보 저장
        PayMethod method = pay.getPayMethod();
        PaymentDetailCreator creator = detailCreators.stream()
                .filter(h -> h.supports() == method)
                .findFirst()
                .orElseThrow(() ->
                        new UnsupportedPayMethodException("지원하지 않는 결제 수단"));
        creator.createDetailOf(pay, paymentContext.getPaymentDetail());
    }
}
