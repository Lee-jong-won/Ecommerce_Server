package jongwon.e_commerce.medium;

import jongwon.e_commerce.payment.application.approve.handler.PaySuccessHandler;
import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.PaySuccessResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.repository.PaymentRepository;
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
        @Sql(value = "/sql/pay-save-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PaySuccessHandlerTest {

    @Autowired
    PaySuccessHandler paySuccessHandler;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void 결제_성공_핸들러가_성공적으로_동작한다(){
        // given

        // 결제 공통 정보
        OffsetDateTime approvedAt = OffsetDateTime.now();
        PayResult.PayResultCommon payResultCommon = PayResult.PayResultCommon.builder().
                payMethod(PayMethod.MOBILE).
                approvedAt(approvedAt).
                build();

        // 결제 상세 정보
        MPPay mpPay = MPPay.from("010-1234-5678", "DONE", "naver");

        // 결제 공통 정보 + 결제 상세 정보 = 결제 결과
        PayResult payResult = PayResult.builder().
                payResultCommon(payResultCommon).paymentDetail(mpPay).build();
        PayApproveSuccess payApproveSuccess = new PayApproveSuccess(payResult);
        Pay pay = paymentRepository.getById(1L);

        // when
        PaySuccessResponse response = (PaySuccessResponse) paySuccessHandler.handle(pay, payApproveSuccess);

        // then
        assertThat(response.getPayStatus()).isEqualTo(PayStatus.COMPLETE);
        assertThat(response.getPayAmount()).isEqualTo(55000);
        assertThat(response.getPayMethod()).isEqualTo(PayMethod.MOBILE);
    }

}