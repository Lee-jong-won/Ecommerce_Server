package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.dto.TossPaymentCancelRequest;
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
    private final TossPaymentGateWay tossPaymentGateWay;

    public void completeSuccess(String paymentKey, String payOrderId,
                                OffsetDateTime approvedAt, String method){
        try{
            paymentResultService.applySuccess(payOrderId, approvedAt, method);
        }catch(DataAccessException e){
            //1. 로그 남기기
            log.error("[CRITICAL] 결제 상태 반영 실패 - payOrderId : {}", payOrderId);

            //2. 보상 트랜잭션 시작

            //2-1.재고 원복
            stockService.increaseStock(payOrderId);

            //2-2.결제 취소
            tossPaymentGateWay.cancel(new TossPaymentCancelRequest(paymentKey, UUID.randomUUID().toString(),
                    "DB 저장 실패로 인한, 결제 취소"));
        }
    }

    public void completeTimeout(String payOrderId){
        try{
            paymentResultService.applyTimeout(payOrderId);
        }catch(DataAccessException e){
            log.error("[CRITICAL] 결제 상태 반영 실패 - payOrderId : {}", payOrderId);
        }
    }

    public void completeFail(String payOrderId){
        try{
            paymentResultService.applyFail(payOrderId);
        }catch(DataAccessException e){
            log.error("[CRITICAL] 결제 상태 반영 실패 - payOrderId : {}", payOrderId);
        }
    }

}
