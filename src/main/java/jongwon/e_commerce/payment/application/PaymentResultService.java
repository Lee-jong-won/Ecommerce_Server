package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.infra.PaymentRepository;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentResultService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final StockService stockService;
    @Transactional
    public void applySuccess(String paymentKey, Long orderId,
                             TossPaymentApproveResponse response) {
        Pay payment = getPayByPaymentKey(paymentKey);
        Order order = getByOrderId(orderId);
        stockService.decreaseStock(order.getOrderId());

        order.markPaid();
        payment.markSuccess();
        payment.setPayMethod(PayMethod.valueOf(response.getMethod()));
        payment.setApprovedAt(response.getApprovedAt());
        payment.setRequestedAt(response.getRequestedAt());
    }

    @Transactional
    public void applyFail(String paymentKey, Long orderId) {
        Pay payment = getPayByPaymentKey(paymentKey);
        Order order = getByOrderId(orderId);

        payment.markFailed();
        order.markFailed();
    }

    @Transactional
    public void applyTimeout(String paymentKey){
        Pay payment = getPayByPaymentKey(paymentKey);
        payment.markSyncTimeout();
    }

    private Pay getPayByPaymentKey(String paymentKey) {
        Pay payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보가 존재하지 않습니다!"));
        return payment;
    }

    private Order getByOrderId(Long orderId){
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotExistException("주문 정보가 존재하지 않습니다!"));
    }
}
