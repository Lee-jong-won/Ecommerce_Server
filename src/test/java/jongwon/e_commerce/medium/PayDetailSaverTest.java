package jongwon.e_commerce.medium;

import jongwon.e_commerce.payment.application.approve.PayDetailSaver;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
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
public class PayDetailSaverTest {

    @Autowired
    PayDetailSaver payDetailSaver;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void 휴대폰_결제_정보가_정상적으로_저장된다(){
        // given
        PaymentDetail paymentDetail = MPPay.from("010-1234-5678", "DONE", "naver");
        Pay pay = paymentRepository.getById(1L);

        // when
        MPPay mpPay = (MPPay) payDetailSaver.save(pay, paymentDetail);

        // then
        assertThat(mpPay.getId()).isNotNull();
        assertThat(mpPay.getPay().getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(mpPay.getPay().getPaymentKey()).isEqualTo("paymentKey");
        assertThat(mpPay.getReceiptUrl()).isEqualTo("naver");
        assertThat(mpPay.getCustomerMobilePhone()).isEqualTo("010-1234-5678");
        assertThat(mpPay.getSettlementStatus()).isEqualTo("DONE");
    }


}
