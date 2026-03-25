package jongwon.e_commerce.medium;

import jongwon.e_commerce.payment.application.approve.handler.PayFailHandler;
import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.PayFailureResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
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
class PayFailHandlerTest {

    @Autowired
    PayFailHandler payFailHandler;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void 결제_실패가_성공적으로_반영된다(){
        // given
        PayApproveFail payApproveFail = new PayApproveFail(
                "INVALID_CARD",
                "카드 정보 오류"
        );
        Pay pay = paymentRepository.getById(1L);

        // when
        PayFailureResponse payFailureResponse = (PayFailureResponse) payFailHandler.handle(pay, payApproveFail);

        // then
        assertThat(payFailureResponse.getCode()).isEqualTo("INVALID_CARD");
        assertThat(payFailureResponse.getMessage()).isEqualTo("카드 정보 오류");
        assertThat(payFailureResponse.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

}