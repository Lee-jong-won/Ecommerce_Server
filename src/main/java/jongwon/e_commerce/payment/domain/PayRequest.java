package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
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
    private String paymentKey;
    private PGType pgType;
    private PayStatus payStatus;
    private long payAmount;
    private Long orderId;

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
        this.payStatus = PayStatus.REFUND;
    }

    public void unknown(){
        if(payStatus == PayStatus.UNKNOWN)
            return;

        if (payStatus != PayStatus.PENDING) {
            throw new InvalidPayStatusException(
                    "UNKNOWN 처리는 결제 진행 중 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        this.payStatus = PayStatus.UNKNOWN;
    }

    public void businessFailed(){
        if(payStatus != PayStatus.PENDING && payStatus != PayStatus.UNKNOWN){
            throw new InvalidPayStatusException(
                    "비즈니스 실패는 결제 진행 중 또는 미확정 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        this.payStatus = PayStatus.BUSINESS_FAILED;
    }

    public void serverFailed(){
        if(payStatus != PayStatus.PENDING && payStatus != PayStatus.UNKNOWN){
            throw new InvalidPayStatusException(
                    "서버 실패는 결제 진행 중 또는 미확정 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        this.payStatus = PayStatus.SERVER_FAILED;
    }

    public void pgFailed(){
        if(payStatus != PayStatus.PENDING && payStatus != PayStatus.UNKNOWN){
            throw new InvalidPayStatusException("PG 실패는 결제 진행 중 또는 미확정 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        this.payStatus = PayStatus.PG_FAILED;
    }

    public void complete(){
        if(payStatus != PayStatus.PENDING && payStatus != PayStatus.UNKNOWN){
            throw new InvalidPayStatusException(
                    "성공은 결제 진행 중 또는 미확정 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        this.payStatus = PayStatus.COMPLETE;
    }

    public static PayRequest from(Long orderId,
                                  String paymentKey,
                                  long payAmount,
                                  PGType pgType){
        return PayRequest.
                builder().
                orderId(orderId).
                payAmount(payAmount).
                payStatus(PayStatus.PENDING).
                paymentKey(paymentKey).
                pgType(pgType).
                build();
    }

}
