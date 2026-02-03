package jongwon.e_commerce.UnitTest.payment.domain;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayTest {

    @Test
    @DisplayName("PENDING 상태에서 결제 성공으로 전이된다")
    void pending_to_success() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.PENDING);

        payment.markSuccess();

        assertEquals(PayStatus.SUCCESS, payment.getPayStatus());
    }

    @Test
    @DisplayName("SYNC_TIMEOUT 상태에서 결제 성공으로 전이된다")
    void syncTimeout_to_success() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.SYNC_TIMEOUT);

        payment.markSuccess();

        assertEquals(PayStatus.SUCCESS, payment.getPayStatus());
    }

    @Test
    @DisplayName("PENDING 상태에서 SYNC_TIMEOUT으로 전이된다")
    void pending_to_syncTimeout() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.PENDING);

        payment.markSyncTimeout();

        assertEquals(PayStatus.SYNC_TIMEOUT, payment.getPayStatus());
    }

    @Test
    @DisplayName("PENDING 상태에서 결제 실패로 전이된다")
    void pending_to_failed() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.PENDING);

        payment.markFailed();

        assertEquals(PayStatus.FAILED, payment.getPayStatus());
    }

    @Test
    @DisplayName("SYNC_TIMEOUT 상태에서 EXPIRED로 전이된다")
    void syncTimeout_to_expired() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.SYNC_TIMEOUT);

        payment.markExpired();

        assertEquals(PayStatus.EXPIRED, payment.getPayStatus());
    }

    @Test
    @DisplayName("SUCCESS 상태에서 결제 취소가 가능하다")
    void success_to_canceled() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.SUCCESS);

        payment.cancel();

        assertEquals(PayStatus.CANCELED, payment.getPayStatus());
    }

    @Test
    @DisplayName("SUCCESS 상태에서는 FAILED로 전이할 수 없다")
    void success_to_failed_invalid() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.SUCCESS);

        assertThrows(
                InvalidPayStatusException.class,
                payment::markFailed
        );
    }

    @Test
    @DisplayName("FAILED 상태에서는 SUCCESS로 전이할 수 없다")
    void failed_to_success_invalid() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.FAILED);

        assertThrows(
                InvalidPayStatusException.class,
                payment::markSuccess
        );
    }

    @Test
    @DisplayName("PENDING 상태가 아니면 SYNC_TIMEOUT으로 전이할 수 없다")
    void invalid_to_syncTimeout() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.SUCCESS);

        assertThrows(
                InvalidPayStatusException.class,
                payment::markSyncTimeout
        );
    }

    @Test
    @DisplayName("SUCCESS 상태가 아니면 취소할 수 없다")
    void cancel_invalid_state() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.PENDING);

        assertThrows(
                InvalidPayStatusException.class,
                payment::cancel
        );
    }
}