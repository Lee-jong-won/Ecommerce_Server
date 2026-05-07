package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaySuccessProcessor {

    private final PaymentCreator paymentCreator;
    private final OrderStockProcessor orderStockProcessor;

    @Transactional
    public void process(PayRequest payRequest, PayResult result) {
        paymentCreator.reflectPaySuccessResult(payRequest, result);
        orderStockProcessor.deductStockOf(payRequest.getOrder().getId());
    }
}
