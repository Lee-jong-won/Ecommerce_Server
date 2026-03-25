package jongwon.e_commerce.medium;

import jongwon.e_commerce.payment.application.approve.PaymentService;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@SqlGroup({
        @Sql(value = "/sql/order-else-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void 주문이_정상적으로_검증된_후_결제_데이터가_정상적으로_생성된다(){
        // given
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey", "test-id", 55000);

        // when
        Pay pay = paymentService.preProcess(attempt);

        // then
        assertThat(pay.getId()).isNotNull();
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
        assertThat(pay.getPayAmount()).isEqualTo(55000);
        assertThat(pay.getPaymentKey()).isEqualTo("paymentKey");
        assertThat(pay.getCreatedAt()).isNotNull();
        assertThat(pay.getUpdatedAt()).isNotNull();
    }

    @Test
    void 결제_성공후_결제결과가_성공적으로_반영된다(){
        // given
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey", "test-id", 55000);
        Pay pay = paymentService.preProcess(attempt);

        PayMethod method = PayMethod.CARD;
        OffsetDateTime approvedAt = OffsetDateTime.now();
        PayResult.PayResultCommon payResultCommon = PayResult.PayResultCommon.builder().
                payMethod(method).
                approvedAt(approvedAt).
                build();


        // when
        Pay updatedPay = paymentService.updatePayResult(pay.getId(), payResultCommon);

        // then
        assertThat(updatedPay.getId()).isEqualTo(pay.getId());
        assertThat(updatedPay.getPayMethod()).isEqualTo(method);
        assertThat(updatedPay.getApprovedAt()).isEqualTo(approvedAt);
    }

}
