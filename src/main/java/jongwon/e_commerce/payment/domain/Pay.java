package jongwon.e_commerce.payment.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pay")
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payId;

    @Column(name = "fk_order_id", nullable = false)
    private Long fkOrderId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "pay_method", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    @Column(name = "pay_amount", nullable = false)
    private long payAmount;

    @Column(name = "pay_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    //결제 인증 이후, 결제 승인을 서버가 프록시할때 만들어짐
    //
    public static Pay create(Long fkOrderId, String orderId, String paymentKey, long payAmount){
        Pay pay = new Pay();
        pay.fkOrderId = fkOrderId;
        pay.orderId = orderId;
        pay.paymentKey = paymentKey;
        pay.payStatus = PayStatus.PENDING;
        pay.payAmount = payAmount;
        return pay;
    }

    //비즈니스 로직
    public void setPayStatus(PayStatus payStatus){
        this.payStatus = payStatus;
    }

    public void setApprovedAt(OffsetDateTime approvedAt){
        this.approvedAt = approvedAt;
    }

    public void setRequestedAt(OffsetDateTime requestedAt){
        this.requestedAt = requestedAt;
    }

    public void setPayMethod(PayMethod payMethod){
        this.payMethod = payMethod;
    }

    // PG 승인 성공 응답 수신
    public void markSuccess() {
        if (payStatus != PayStatus.PENDING &&
                payStatus != PayStatus.SYNC_TIMEOUT) {
            throw new InvalidPayStatusException(
                    "결제 성공 처리 불가 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.SUCCESS);
    }

    // PG 승인 실패 응답 수신
    public void markFailed() {
        if (payStatus != PayStatus.PENDING &&
                payStatus != PayStatus.SYNC_TIMEOUT) {
            throw new InvalidPayStatusException(
                    "결제 실패 처리 불가 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.FAILED);
    }

    // 승인 API 응답 미수신
    public void markSyncTimeout() {
        if (payStatus != PayStatus.PENDING) {
            throw new InvalidPayStatusException(
                    "SYNC_TIMEOUT 전환 불가 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.SYNC_TIMEOUT);
    }

    // PG 정책상 만료 (30분 경과)
    public void markExpired() {
        if (payStatus != PayStatus.PENDING &&
                payStatus != PayStatus.SYNC_TIMEOUT) {
            throw new InvalidPayStatusException(
                    "EXPIRED 전환 불가 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.EXPIRED);
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
