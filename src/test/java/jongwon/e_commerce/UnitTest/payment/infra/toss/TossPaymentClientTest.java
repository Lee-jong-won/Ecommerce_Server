package jongwon.e_commerce.UnitTest.payment.infra.toss;

import com.github.tomakehurst.wiremock.WireMockServer;
import jongwon.e_commerce.external.http.client.HttpClientFactory;
import jongwon.e_commerce.external.http.policy.ConnectionPolicy;
import jongwon.e_commerce.external.http.policy.HttpClientPolicy;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiTimeoutException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentSystemException.TossPaymentSystemException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import jongwon.e_commerce.payment.infra.toss.TossPaymentClient;
import jongwon.e_commerce.payment.infra.toss.TossPaymentClientErrorHandler;
import jongwon.e_commerce.payment.infra.toss.TossPaymentErrorMapper;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TossPaymentClientTest {
    WireMockServer wireMockServer;
    TossPaymentClientErrorHandler tossPaymentClientErrorHandler;
    HttpClientPolicy basePolicy;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        tossPaymentClientErrorHandler = new TossPaymentClientErrorHandler(
                new ObjectMapper(), new TossPaymentErrorMapper()
        );

        basePolicy = HttpClientPolicy.builder()
                .connectionPoolPolicy(
                        ConnectionPolicy.builder().
                                connectTimeout(Timeout.ofSeconds(1))
                                        .socketTimeout(Timeout.ofSeconds(1))
                                .build()
                )
                .build();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void 결제승인_성공() {
        //given
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "method": "CARD",
                          "requestedAt": "2024-01-17T12:00:02+09:00",
                          "approvedAt": "2024-01-17T12:00:00+09:00"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        TossPaymentApproveResponse response = client.approve(request);

        // then
        assertThat(response.getMethod()).isEqualTo("CARD");
        assertThat(response.getRequestedAt())
                .isEqualTo(OffsetDateTime.parse("2024-01-17T12:00:02+09:00"));
        assertThat(response.getApprovedAt())
                .isEqualTo(OffsetDateTime.parse("2024-01-17T12:00:00+09:00"));
    }

    @Test
    void 결제승인_중_ReadTimeout_발생시_TossApiTimeoutException_발생() {
        // given
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy
        );

        wireMockServer.stubFor(
                post(urlEqualTo("/payments/confirm"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(3000) // read timeout 유도
                        )
        );

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        // when & then
        assertThatThrownBy(() -> client.approve(request))
                .isInstanceOf(TossApiTimeoutException.class);
    }

    @Test
    void PG와_연결이_안되면_network_exception() {
        TossPaymentClient client = createClient(
                "http://localhost:65535",
                basePolicy
        );

        TossPaymentApproveRequest request =
                new TossPaymentApproveRequest(
                        "abcd",
                        "paykey1234",
                        "축구화외 1건",
                        "idem-key",
                        5000
                );

        assertThatThrownBy(() -> client.approve(request))
                .isInstanceOf(TossApiNetworkException.class);
    }

    @Test
    void 비즈니스_관련_에러_응답이_오면_TossPaymentUserFaultException(){
        //given
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "code": "INVALID_REQUEST",
                          "message": "잘못된 요청입니다."
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.approve(request))
                .isInstanceOf(TossPaymentUserFaultException.class);
    }

    @Test
    void 서버처리_관련_에러_응답이_오면_TossRetryableException(){
        //given
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "message": "너무 많은 요청이 몰렸습니다. 다시 시도해주세요"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.approve(request))
                .isInstanceOf(TossPaymentRetryableException.class);
    }

    @Test
    void 인증_관련_에러_응답이_오면_TossSystemException(){
        //given
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "code" : "UNAUTHORIZED_KEY",
                            "message" : "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.approve(request))
                .isInstanceOf(TossPaymentSystemException.class);
    }

    @Test
    void API_SPEC_변화시_TossPaymentException(){
        //given
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "ApiCode" : "UNAUTHORIZED_KEY",
                            "ApiMessage" : "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.approve(request))
                .isInstanceOf(TossPaymentException.class);
    }

    @Test
    void 새로운_에러코드_추가시_TossPaymentException(){
        //given
        TossPaymentClient client = createClient(
                "http://localhost:" + wireMockServer.port(),
                basePolicy
        );

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "code" : "NEW_ERROR_CODE",
                            "message" : "새로 추가된 에러코드 입니다"
                        }
                    """)
                ));

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getIdempotencyKey()).thenReturn("idem-key");

        //when
        assertThatThrownBy(() -> client.approve(request))
                .isInstanceOf(TossPaymentException.class);
    }


    private TossPaymentClient createClient(String baseUrl,
                                           HttpClientPolicy policy) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(
                        new HttpComponentsClientHttpRequestFactory(
                                HttpClientFactory.create(policy)
                        )
                )
                .defaultStatusHandler(HttpStatusCode::isError, tossPaymentClientErrorHandler)
                .build();

        return new TossPaymentClient(
                restClient,
                new ObjectMapper()
        );
    }
}