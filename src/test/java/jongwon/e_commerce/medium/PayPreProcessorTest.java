package jongwon.e_commerce.medium;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
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
        @Sql(value = "/sql/order-else-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class PayPreProcessorTest {

    @Autowired
    private PayPreprocessor payPreprocessor;

    @Test
    void 주문이_정상적으로_검증된_후_결제_데이터가_정상적으로_생성된다(){
        // given
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey", "test-id", 55000);

        // when
        Pay pay = payPreprocessor.preProcess(attempt);

        // then
        assertThat(pay.getId()).isNotNull();
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
        assertThat(pay.getPayAmount()).isEqualTo(55000);
        assertThat(pay.getPaymentKey()).isEqualTo("paymentKey");
    }


}
