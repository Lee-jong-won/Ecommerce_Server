package jongwon.e_commerce.large;

import jongwon.e_commerce.payment.application.approve.external.PayApprovalExecutor;
import jongwon.e_commerce.payment.controller.PaySuccessResponse;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@SqlGroup({
        @Sql(value = "/sql/order-create-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class PaymentApprovalControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private RestClient restClient;
    @MockitoBean
    private PayApprovalExecutor payApprovalExecutor;
    String url;
    @BeforeEach
    void init(){
        url = "http://localhost:" + port + "/api/payment";
    }

    @Test
    void 사용자에게_결제성공이_일어날_수_있다() {
        // given
        PayApproveAttempt attempt = new PayApproveAttempt(
                "paymentKey", "test-id", 55000);

        PayResult.PayResultCommon payResultCommon =
                PayResult.PayResultCommon.builder()
                        .payMethod(PayMethod.MOBILE)
                        .approvedAt(OffsetDateTime.now())
                        .build();

        MPPay mpPay = MPPay.builder()
                .customerMobilePhone("010-1234-5678")
                .settlementStatus("DONE")
                .receiptUrl("naver")
                .build();

        PayResult payResult = PayResult.builder()
                .payResultCommon(payResultCommon)
                .paymentDetail(mpPay)
                .build();

        PayApproveSuccess payApproveSuccess = new PayApproveSuccess(payResult);

        when(payApprovalExecutor.executePayApprove(any()))
                .thenReturn(payApproveSuccess);

        // when
        ResponseEntity<PaySuccessResponse> response = restClient.post()
                .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                                .body(attempt)
                                        .retrieve()
                                                .toEntity(PaySuccessResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PaySuccessResponse body =  response.getBody();
        assertThat(body.getPayStatus()).isEqualTo(PayStatus.COMPLETE);
        assertThat(body.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(body.getPayAmount()).isEqualTo(55000);
    }

}
