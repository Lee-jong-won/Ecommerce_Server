package jongwon.e_commerce.payment.application.approve.result.persistence;

import jongwon.e_commerce.payment.domain.Pay;

public interface PaymentRecorder {
    void record(Pay pay);
}
