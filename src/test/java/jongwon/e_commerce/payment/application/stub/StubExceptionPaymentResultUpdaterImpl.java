package jongwon.e_commerce.payment.application.stub;

import jongwon.e_commerce.payment.application.PaymentResultUpdater;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.OffsetDateTime;

public class StubExceptionPaymentResultUpdaterImpl implements PaymentResultUpdater {
    @Override
    public void applySuccess(String orderId, OffsetDateTime approvedAt, String method) {
        throw new DataAccessResourceFailureException("DB 연결 실패");
    }
    @Override
    public void applyFail(String orderId) {
        throw new DataAccessResourceFailureException("DB 연결 실패");
    }

    @Override
    public void applyTimeout(String orderId) {
        throw new DataAccessResourceFailureException("DB 연결 실패");
    }
}
