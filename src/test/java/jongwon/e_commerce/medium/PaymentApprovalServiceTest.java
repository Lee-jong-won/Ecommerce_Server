package jongwon.e_commerce.medium;

import jongwon.e_commerce.mock.stub.StubPaymentRestApproveClientNormal;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.application.approve.PaymentService;
import jongwon.e_commerce.payment.application.approve.external.DefaultPayApproveExceptionTranslator;
import jongwon.e_commerce.payment.application.approve.external.PayApprovalExecutor;
import jongwon.e_commerce.payment.application.approve.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.PaySuccessResponse;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@SqlGroup({
        @Sql(value = "/sql/order-else-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class PaymentApprovalServiceTest {
    PaymentApprovalService paymentApprovalService;
    PayApprovalExecutor payApprovalExecutor;
    @Autowired
    PaymentService paymentService;
    @Autowired
    List<PayOutcomeHandler> outcomeHandlers;

    @Test
    void 결제가_정상적으로_성공된다(){
        // given
        payApprovalExecutor = payApprovalExecutor.builder().
                paymentApproveClient(new StubPaymentRestApproveClientNormal()).
                payApproveExceptionTranslator(new DefaultPayApproveExceptionTranslator()).build();

        paymentApprovalService = PaymentApprovalService.builder().
                paymentService(paymentService).
                payApprovalExecutor(payApprovalExecutor).
                outcomeHandlers(outcomeHandlers).
                build();

        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "test-id", 55000);

        // when
        PaySuccessResponse response = (PaySuccessResponse) paymentApprovalService.approvePayment(attempt);

        // then
        assertThat(response.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(response.getPayStatus()).isEqualTo(PayStatus.COMPLETE);
        assertThat(response.getPayAmount()).isEqualTo(55000);
    }



}
