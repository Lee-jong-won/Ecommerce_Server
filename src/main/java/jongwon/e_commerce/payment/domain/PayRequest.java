package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.exception.DuplicatePayAttemptException;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Builder
@Getter
public class PayRequest {

    private Long id;
    private String orderId;
    private String paymentKey;
    private String pgType;
    private PayStatus payStatus;
    private long payAmount;
    private Order order;

    public void setPayStatus(PayStatus payStatus){
        this.payStatus = payStatus;
    }

    // 결제 취소 (승인 성공 후에만 가능)
    public void refund() {
        if (payStatus != PayStatus.COMPLETE) {
            throw new InvalidPayStatusException(
                    "취소는 결제 성공 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.REFUND);
    }

    public void unknown(){
        if (payStatus != PayStatus.PENDING) {
            throw new InvalidPayStatusException(
                    "UNKNOWN 처리는 결제 진행 중 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.UNKNOWN);
    }

    public void businessFailed(){
        if(payStatus != PayStatus.PENDING){
            throw new InvalidPayStatusException(
                    "실패는 결제 진행 중 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.BUSINESS_FAILED);
    }

    public void serverFailed(){
        if(payStatus != PayStatus.PENDING){
            throw new InvalidPayStatusException(
                    "실패는 결제 진행 중 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.SERVER_FAILED);
    }

    public void resetToPending(){
        if(payStatus != PayStatus.SERVER_FAILED && payStatus != PayStatus.UNKNOWN){
            throw new InvalidPayStatusException(
                    "진행중으로 변경은 서버 실패 또는 결과 알 수 없음 상태에서만 가능합니다. 현재 상태: " + payStatus);
        }
        setPayStatus(PayStatus.PENDING);
    }

    public void complete(){
        if(payStatus != PayStatus.PENDING){
            throw new InvalidPayStatusException(
                    "성공은 결제 진행 중 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.COMPLETE);
    }


    public static String createOrderId(){
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                .format(LocalDateTime.now());
        String random = UUID.randomUUID().toString().substring(0, 8);
        return "PAY-" + timestamp + "-" + random;
    }

    public static PayRequest from(Order order,
                                  String orderId,
                                  String paymentKey,
                                  long payAmount,
                                  String pgType){
        return PayRequest.
                builder().
                order(order).
                payAmount(payAmount).
                payStatus(PayStatus.PENDING).
                orderId(orderId).
                paymentKey(paymentKey).
                pgType(pgType).
                build();
    }

}
