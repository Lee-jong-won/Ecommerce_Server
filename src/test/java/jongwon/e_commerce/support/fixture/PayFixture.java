package jongwon.e_commerce.support.fixture;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class PayFixture {

    @Builder.Default
    private String paymentKey = "PAY-" + System.nanoTime();

    @Builder.Default
    private PayMethod payMethod = PayMethod.MOBILE;

    @Builder.Default
    private Order order = null; // 외부 주입

    @Builder.Default
    private long payAmount = 10000L;

    @Builder.Default
    private PayStatus payStatus = PayStatus.PENDING;

    @Builder.Default
    private OffsetDateTime approvedAt = null;

    public Pay create() {
        return Pay.builder()
                .paymentKey(paymentKey)
                .payMethod(payMethod)
                .order(order)
                .payAmount(payAmount)
                .payStatus(payStatus)
                .build();
    }
}
