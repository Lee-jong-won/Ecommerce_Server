package jongwon.e_commerce.payment.application;

import java.time.OffsetDateTime;

public interface PaymentCompleter {
    void completeSuccess(String paymentKey,
                                String orderId,
                                OffsetDateTime approvedAt,
                                String method);
    void completeTimeout(String paymentKey, String orderId);
    void completeFail(String paymentKey, String orderId);
}
