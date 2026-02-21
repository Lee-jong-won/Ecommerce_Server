package jongwon.e_commerce.payment.application.stub;

import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.PaymentResultService;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.OffsetDateTime;

public class StubExceptionPaymentResultService extends PaymentResultService {
    public StubExceptionPaymentResultService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        super(paymentRepository, orderRepository);
    }

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
