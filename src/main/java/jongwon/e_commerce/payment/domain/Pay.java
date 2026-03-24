package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Builder
public class Pay {

    private Long id;
    private String paymentKey;
    private PayMethod payMethod;
    private Order order;
    private long payAmount;
    private PayStatus payStatus;
    private OffsetDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public void timeout(){
        if (payStatus != PayStatus.PENDING) {
            throw new InvalidPayStatusException(
                    "타임아웃은 결제 진행 중 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.TIME_OUT);
    }

    public void failed(){
        if(payStatus != PayStatus.PENDING){
            throw new InvalidPayStatusException(
                    "실패는 결제 진행 중 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.FAILED);
    }

    public void comeplete(){
        if(payStatus != PayStatus.PENDING){
            throw new InvalidPayStatusException(
                    "성공은 결제 진행 중 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.COMPLETE);
    }

    public static Pay from(Order order,
                           String paymentKey,
                           long amount){
        return Pay.builder().
                order(order).
                payStatus(PayStatus.PENDING).
                paymentKey(paymentKey).
                payAmount(amount).
                build();
    }

    public Pay reflectPaySuccess(PayResult.PayResultCommon payResultCommon){
        comeplete();
        return Pay.builder()
                .id(id)
                .order(order)
                .paymentKey(paymentKey)
                .payAmount(payAmount)
                .payStatus(payStatus)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .payMethod(payResultCommon.getPayMethod())
                .approvedAt(payResultCommon.getApprovedAt())
                .build();
    }
}
