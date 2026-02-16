package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
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
        Pay pay = paymentRepository.findByOrderId(payOrderId);

        //가격이 일치 하지 않으면, 예외 throw
        if (pay.getPayAmount() != amount)
            throw new InvalidAmountException();

        //일치 하면, 결제 진행 중으로 결제 상태 변경
        pay.markPending();

        // 재고 감소
        stockService.decreaseStock(payOrderId);
    }
}
