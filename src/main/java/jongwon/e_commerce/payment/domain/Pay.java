package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentContext;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@SuperBuilder
public abstract class Pay {
    private Long payId;
    private String paymentKey;
    private PayMethod payMethod;
    private Order order;
    private long payAmount;
    private PayStatus payStatus;
    private OffsetDateTime approvedAt;

    public void setOrder(Order order){this.order = order;}
    public void setPayStatus(PayStatus payStatus){
        this.payStatus = payStatus;
    }

    // 결제 취소 (승인 성공 후에만 가능)
    public void cancel() {
        if (payStatus != PayStatus.SUCCESS) {
            throw new InvalidPayStatusException(
                    "취소는 결제 성공 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.CANCELED);
    }
}
