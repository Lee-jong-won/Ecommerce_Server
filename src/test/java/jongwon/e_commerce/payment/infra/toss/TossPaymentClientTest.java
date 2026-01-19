package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.exception.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.TossPaymentApprovalClientFailException;
import jongwon.e_commerce.payment.exception.TossPaymentApprovalPGFailException;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(TossPaymentClient.class)
@Import(TossPaymentClientTest.TestConfig.class)
class TossPaymentClientTest {

    @Autowired
    private RestClient restClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private TossPaymentClient tossPaymentClient;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public RestClient tossRestClient(RestClient.Builder builder) {
            return builder.build();
        }
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    private final String PAYMENT_KEY = "pk_test_123";
    private final String ORDER_ID = "order_1";
    private final Long AMOUNT = 10000L;

    @Test
    void 결제승인_성공() {
        // given
        String responseBody = """
        {
            "status": "DONE",
            "method": "카드",
            "requestedAt": "2024-01-17T12:00:00+09:00",
            "approvedAt": "2024-01-17T12:00:02+09:00"
        }
        """;

        server.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.paymentKey").value(PAYMENT_KEY))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.amount").value(AMOUNT))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // when
        TossPaymentApproveResponse response =
                tossPaymentClient.approvePayment(PAYMENT_KEY, ORDER_ID, AMOUNT);

        // then
        assertEquals("DONE", response.getStatus());
        assertEquals( "카드", response.getMethod());
        assertEquals(OffsetDateTime.parse("2024-01-17T12:00:00+09:00").toInstant(), response.getRequestedAt().toInstant());
        assertEquals(OffsetDateTime.parse("2024-01-17T12:00:02+09:00").toInstant(), response.getApprovedAt().toInstant());
    }

    @Test
    void 잘못된_요청이면_ClientFailException_발생() {
        // given
        server.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // when & then
        assertThatThrownBy(() ->
                tossPaymentClient.approvePayment(PAYMENT_KEY, ORDER_ID, AMOUNT))
                .isInstanceOf(TossPaymentApprovalClientFailException.class);
    }

    @Test
    void PG서버_오류면_PGFailException_발생() {
        // given
        server.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // when & then
        assertThatThrownBy(() ->
                tossPaymentClient.approvePayment(PAYMENT_KEY, ORDER_ID, AMOUNT))
                .isInstanceOf(TossPaymentApprovalPGFailException.class);
    }

    @Test
    void 네트워크_장애면_NetworkException_발생() {
        // given
        server.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(request -> {
                    throw new ResourceAccessException("timeout");
                });

        // when & then
        assertThatThrownBy(() ->
                tossPaymentClient.approvePayment(PAYMENT_KEY, ORDER_ID, AMOUNT))
                .isInstanceOf(TossApiNetworkException.class);
    }
}