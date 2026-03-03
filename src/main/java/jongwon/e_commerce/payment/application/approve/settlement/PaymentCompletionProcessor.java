package jongwon.e_commerce.payment.application.approve.settlement;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.application.approve.settlement.handler.success.OrderStockProcessor;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletionProcessor {

    private final OrderStockProcessor orderStockProcessor;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void complete(Pay pay){
        Order order = pay.getOrder();
        order.paid();
        orderStockProcessor.deductStockBy(pay);
        paymentRepository.save(pay);
    }
}
