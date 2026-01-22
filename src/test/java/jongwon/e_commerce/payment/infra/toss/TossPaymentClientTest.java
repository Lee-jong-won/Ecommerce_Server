package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.exception.TossPaymentErrorMapper;
import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiTimeoutException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentSystemException.TossPaymentSystemException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import jongwon.e_commerce.payment.presentation.dto.TossErrorResponse;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(TossPaymentClient.class)
@Import({TossPaymentClientTest.TestConfig.class, TossPaymentErrorMapper.class})
class TossPaymentClientTest {

    @Autowired
    TossPaymentErrorMapper tossPaymentErrorMapper;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    TossPaymentClient tossPaymentClient;

    @Autowired
    ObjectMapper objectMapper;

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
    @BeforeEach
    void setUp() {
        tossPaymentErrorMapper = new TossPaymentErrorMapper();
    }

    @Test
    void 결제승인_성공() {
        // given
        TossPaymentApproveResponse response = new TossPaymentApproveResponse("카드",
                OffsetDateTime.parse("2024-01-17T12:00:00+09:00"), OffsetDateTime.parse("2024-01-17T12:00:02+09:00"));

        server.expect(requestTo("/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withSuccess(
                                objectMapper.writeValueAsString(response),
                                MediaType.APPLICATION_JSON
                        )
                );

        // when
        TossPaymentApproveResponse result =
                tossPaymentClient.approvePayment("payKey", "orderId", 1000L);

        // then
        assertEquals("카드", response.getMethod());
        assertEquals(OffsetDateTime.parse("2024-01-17T12:00:00+09:00"), response.getRequestedAt());
        assertEquals(OffsetDateTime.parse("2024-01-17T12:00:02+09:00"), response.getApprovedAt());
    }

    @Test
    void PG사에서_USER_FAULT_응답시_TossPaymentUserFaultException으로_변환된다() {
        // given
        TossErrorResponse error =
                new TossErrorResponse("INVALID_REQUEST", "잘못된 요청");

        server.expect(requestTo("/confirm"))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(error))
                );

        // when & then
        assertThrows(
                TossPaymentUserFaultException.class,
                () -> tossPaymentClient.approvePayment("payKey", "orderId", 1000L)
        );
    }

    @Test
    void PG사에서_REJECT_응답시_TossPaymentUserFaultException으로_변환된다() {
        // given
        TossErrorResponse error =
                new TossErrorResponse("REJECT_ACCOUNT_PAYMENT", "잔액부족으로 결제에 실패했습니다.");

        server.expect(requestTo("/confirm"))
                .andRespond(
                        withStatus(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(error))
                );

        // when & then
        assertThrows(
                TossPaymentUserFaultException.class,
                () -> tossPaymentClient.approvePayment("payKey", "orderId", 1000L)
        );
    }

    @Test
    void PG사에서_Retry응답시_TossPaymentRetryableException으로_변환된다() {
        // given
        TossErrorResponse error =
                new TossErrorResponse("TOO_MANY_REQUESTS", "요청량이 초과되었습니다. 일정 시간 이후 시도해주세요.");

        server.expect(requestTo("/confirm"))
                .andRespond(
                        withStatus(HttpStatus.TOO_MANY_REQUESTS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(error))
                );

        // when & then
        assertThrows(
                TossPaymentRetryableException.class,
                () -> tossPaymentClient.approvePayment("payKey", "orderId", 1000L)
        );
    }

    @Test
    void 인증오류코드면_예외로_변환된다() throws Exception {
        // given
        TossErrorResponse error =
                new TossErrorResponse("INVALID_API_KEY", "인증 실패");

        server.expect(requestTo("/confirm"))
                .andRespond(
                        withStatus(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(error))
                );

        // when & then
        assertThrows(
                TossPaymentSystemException.class,
                () -> tossPaymentClient.approvePayment("payKey", "orderId", 1000L)
        );
    }

    @Test
    void 소켓타임아웃이면_TossApiTimeoutException으로_변환된다() {
        // given
        server.expect(requestTo("/confirm"))
                .andRespond(request -> {
                    throw new ResourceAccessException(
                            "timeout",
                            new SocketTimeoutException()
                    );
                });

        // when & then
        assertThrows(
                TossApiTimeoutException.class,
                () -> tossPaymentClient.approvePayment("payKey", "orderId", 1000L)
        );
    }

    @Test
    void 네트워크_오류면_TossApiNetworkException으로_변환된다() {
        // given
        server.expect(requestTo("/confirm"))
                .andRespond(request -> {
                    throw new ResourceAccessException(
                            "connection reset",
                            new IOException()
                    );
                });

        // when & then
        assertThrows(
                TossApiNetworkException.class,
                () -> tossPaymentClient.approvePayment("payKey", "orderId", 1000L)
        );
    }

    @Test
    void 에러응답_JSON파싱실패시_UNKNOWN_ERROR로_처리된다() {
        // given
        server.expect(requestTo("/confirm"))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("invalid-json")
                );

        // when & then
        assertThrows(
                TossPaymentException.class,
                () -> tossPaymentClient.approvePayment("payKey", "orderId", 1000L)
        );
    }

}