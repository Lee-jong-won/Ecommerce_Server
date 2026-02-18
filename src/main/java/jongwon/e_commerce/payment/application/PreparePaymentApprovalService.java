package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PreparePaymentApprovalService {

    private final StockService stockService;
    private final PaymentRepository paymentRepository;

    public void preparePaymentApproval(String payOrderId, long amount) {
        //orderId로 Pay 조회
        Pay payment = paymentRepository.findByOrderId(payOrderId).orElseThrow(
                () -> new PaymentNotFoundException("해당 주문 ID에 대응되는 결제 정보가 존재하지 않습니다.")
        );

        //가격이 일치 하지 않으면, 예외 throw
        if (payment.getPayAmount() != amount)
            throw new InvalidAmountException();

        //일치 하면, 결제 진행 중으로 결제 상태 변경
        payment.markPending();

        // 재고 감소
        stockService.decreaseStock(payOrderId);
    }
}
