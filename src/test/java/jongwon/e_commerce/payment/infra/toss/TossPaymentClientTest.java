package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.exception.TossPaymentErrorMapper;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import jongwon.e_commerce.payment.presentation.dto.TossErrorResponse;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(TossPaymentClient.class)
@Import(TossPaymentClientTest.TestConfig.class)
class TossPaymentClientTest {

    @MockitoBean
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

    @Test
    void 결제승인_성공() {
        // given
        TossPaymentApproveResponse response = new TossPaymentApproveResponse("카드",
                OffsetDateTime.parse("2024-01-17T12:00:00+09:00"),OffsetDateTime.parse("2024-01-17T12:00:02+09:00"));

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
        assertEquals( "카드", response.getMethod());
        assertEquals(OffsetDateTime.parse("2024-01-17T12:00:00+09:00"), response.getRequestedAt());
        assertEquals(OffsetDateTime.parse("2024-01-17T12:00:02+09:00"), response.getApprovedAt());
    }

    @Test
    void XX_에러시_변환()  {
        // given
        TossErrorResponse error =
                new TossErrorResponse("INVALID_REQUEST", "잘못된 요청");

        server.expect(requestTo("/confirm"))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(error))
                );

        when(tossPaymentErrorMapper.map("INVALID_REQUEST"))
                .thenReturn(new TossPaymentUserFaultException(ErrorCode.INVALID_REQUEST));

        // when & then
        assertThrows(
                TossPaymentUserFaultException.class,
                () -> tossPaymentClient.approvePayment("payKey", "orderId", 1000L)
        );
    }



}