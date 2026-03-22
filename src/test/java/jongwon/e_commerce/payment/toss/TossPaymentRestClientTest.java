package jongwon.e_commerce.payment.toss;

import com.github.tomakehurst.wiremock.WireMockServer;
import jongwon.e_commerce.external.http.client.HttpClientFactory;
import jongwon.e_commerce.external.http.policy.ConnectionPolicy;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.config.TossPaymentRetryConfig;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.external.TossPaymentClientException;
import jongwon.e_commerce.payment.exception.external.TossPaymentServerException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TossPaymentRestClientTest {

    WireMockServer wireMockServer;
    TossPaymentRestClient tossPaymentHttpClient;

    @BeforeEach
    void setUp(){
        // wire mock 서버 시작
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        // 타임아웃을 위한 정책 설정
        HttpClient httpClient = HttpClientFactory.create(List.of(
                ConnectionPolicy.builder().socketTimeout(Timeout.ofMilliseconds(1000))
                        .build()
        ));

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMockServer.port())
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();

        TossPaymentRetryConfig tossPaymentRetryConfig = new TossPaymentRetryConfig();
        RetryTemplate tossPaymentRetryTemplate = tossPaymentRetryConfig.paymentApproveRetryTemplate();

        tossPaymentHttpClient = new TossPaymentRestClient(restClient, tossPaymentRetryTemplate);
    }

    @AfterEach
    void tearDown(){
        wireMockServer.stop();
    }

    @Test
    void 정상적으로_결제가_승인된다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // wireMock가 정상 응답 반환하도록 설정
        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "approvedAt": "2024-02-13T12:18:14+09:00",
                          "method": "휴대폰"
                        }
                        """)
                ));

        String idempotencyKey = UUID.randomUUID().toString();

        //when
        TossPaymentApproveResponse response = tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey);

        //then
        wireMockServer.verify(1, postRequestedFor(urlPathEqualTo("/payments/confirm"))
                .withHeader("Idempotency-Key", equalTo(idempotencyKey)));
        assertEquals(OffsetDateTime.parse("2024-02-13T03:18:14Z"), response.getApprovedAt());
        assertEquals("휴대폰", response.getMethod());
    }

    @Test
    void 클라이언트_에러_응답시_RestClientResponseException이_발생한다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // wireMock 클라이언트 에러 응답 반환하도록 설정
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
        String idempotencyKey = UUID.randomUUID().toString();

        //when && then
        assertThrows(RestClientResponseException.class,
                () -> tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey));
        wireMockServer.verify(1, postRequestedFor(urlPathEqualTo("/payments/confirm"))
                .withHeader("Idempotency-Key", equalTo(idempotencyKey)));
    }

    @Test
    void 재시도시에도_에러_응답만_돌아오는_경우_RestClientResponseException을_throw한다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // wireMock가 서버 처리 에러 응답을 반환하도록 설정
        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "code" : "FAILED_INTERNAL_SYSTEM_PROCESSING",
                            "message" : "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."
                        }
                    """)
                ));

        String idempotencyKey = UUID.randomUUID().toString();

        // when && then
        assertThrows(RestClientResponseException.class,
                () -> tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey));
        wireMockServer.verify(3, postRequestedFor(urlPathEqualTo("/payments/confirm"))
                .withHeader("Idempotency-Key", equalTo(idempotencyKey)));
    }

    @Test
    void 재시도시에_처음_에러응답을_받은후_정상응답을_받는다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // wireMock가 서버 처리 에러 응답을 반환하도록 설정
        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                        .inScenario("retry-scenario")
                        .whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "code" : "FAILED_INTERNAL_SYSTEM_PROCESSING",
                            "message" : "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."
                        }
                    """)
                ).willSetStateTo("second-call"));

        // wireMock가 정상 응답 반환하도록 설정
        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                        .inScenario("retry-scenario")
                        .whenScenarioStateIs("second-call")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "approvedAt": "2024-02-13T12:18:14+09:00",
                          "method": "휴대폰"
                        }
                        """)
                ));

        String idempotencyKey = UUID.randomUUID().toString();

        // when && then
        assertNotNull(tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey));
        wireMockServer.verify(2, postRequestedFor(urlPathEqualTo("/payments/confirm"))
                .withHeader("Idempotency-Key", equalTo(idempotencyKey)));
    }

    @Test
    void 재시도시에도_계속_타임아웃이_발생한경우_ResourceAccessException이_발생한다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // wireMock가 시간안에 응답을 반환하지 않도록 설정
        wireMockServer.stubFor(
                post(urlEqualTo("/payments/confirm"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(1500) // read timeout 유도
                        )
        );

        //when && then
        assertThrows(ResourceAccessException.class, () -> tossPaymentHttpClient.callPayApprovalApi(request, UUID.randomUUID().toString()));
    }

    @Test
    void 타임아웃_이후_재시도시에_정상응답을_받는다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .inScenario("timeout-retry")
                .whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withFixedDelay(2000)) // 🔥 3초 지연 → timeout 유도
                .willSetStateTo("success"));

        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .inScenario("timeout-retry")
                .whenScenarioStateIs("success")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
        {
          "approvedAt": "2024-02-13T12:18:14+09:00",
          "method": "휴대폰"
        }
        """)));
        String idempotencyKey = UUID.randomUUID().toString();

        // when && then
        assertNotNull(tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey));
        wireMockServer.verify(2, postRequestedFor(urlPathEqualTo("/payments/confirm"))
                .withHeader("Idempotency-Key", equalTo(idempotencyKey)));
    }

}