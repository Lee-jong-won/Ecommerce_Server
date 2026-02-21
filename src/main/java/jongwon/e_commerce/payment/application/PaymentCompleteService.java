package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.dto.TossPaymentCancelRequest;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import jongwon.e_commerce.payment.toss.TossPaymentGateWay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCompleteService {

    private final StockService stockService;
    private final PaymentResultService paymentResultService;

    public void completeSuccess(String paymentKey,
                                String orderId,
                                OffsetDateTime approvedAt,
                                String method){
        try{
            paymentResultService.applySuccess(orderId, approvedAt, method);
            stockService.decreaseStock(orderId);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentKey, orderId, e);
        }
    }

    public void completeTimeout(String paymentKey, String orderId){
        try{
            paymentResultService.applyTimeout(orderId);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentKey, orderId, e);
        }
    }

    public void completeFail(String paymentKey, String orderId){
        try{
            paymentResultService.applyFail(orderId);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentKey, orderId, e);
        }
    }
}
