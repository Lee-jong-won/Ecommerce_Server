package jongwon.e_commerce.medium;

import jongwon.e_commerce.payment.application.approve.handler.PayTimeoutHandler;
import jongwon.e_commerce.payment.controller.PayFailureResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@SqlGroup({
        @Sql(value = "/sql/pay-save-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PayTimeoutHandlerTest {

    @Autowired
    PayTimeoutHandler payTimeoutHandler;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void 결제에_타임아웃이_성공적으로_반영된다(){
        // given
        PayApproveTimeout payApproveTimeout = new PayApproveTimeout();
        Pay pay = paymentRepository.getById(1L);

        // when
        PayFailureResponse payFailureResponse = (PayFailureResponse) payTimeoutHandler.handle(pay, payApproveTimeout);

        // then
        assertThat(payFailureResponse.getPayStatus()).isEqualTo(PayStatus.TIME_OUT);
        assertThat(payFailureResponse.getCode()).isEqualTo("PAYMENT_TIMEOUT");
        assertThat(payFailureResponse.getMessage()).isEqualTo("결제 시도가 많습니다. 다시 시도해주세요");
    }


}