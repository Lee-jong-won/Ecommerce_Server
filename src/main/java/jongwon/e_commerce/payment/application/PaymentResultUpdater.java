package jongwon.e_commerce.payment.application;

import java.time.OffsetDateTime;

public interface PaymentResultUpdater {
    void applySuccess(String orderId, OffsetDateTime approvedAt, String method);
    void applyFail(String orderId);
    void applyTimeout(String orderId);
}
