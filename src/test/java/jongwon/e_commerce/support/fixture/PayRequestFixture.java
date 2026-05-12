package jongwon.e_commerce.support.fixture;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PayRequestFixture {

    @Builder.Default
    private String paymentKey = "PAY-" + System.nanoTime();

    @Builder.Default
    private Order order = null; // 외부 주입

    @Builder.Default
    private long payAmount = 10000L;

    @Builder.Default
    private PayStatus payStatus = PayStatus.PENDING;

    @Builder.Default
    private PGType pgType = PGType.TOSS;

    public PayRequest create() {
        return PayRequest.builder()
                .paymentKey(paymentKey)
                .order(order)
                .payAmount(payAmount)
                .payStatus(payStatus)
                .pgType(pgType)
                .build();
    }
}
