package jongwon.e_commerce.payment.toss;

import com.github.tomakehurst.wiremock.WireMockServer;
import jongwon.e_commerce.external.http.client.HttpClientFactory;
import jongwon.e_commerce.external.http.policy.ConnectionPolicy;
import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
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
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class TossPaymentHttpClientImplTest {

    WireMockServer wireMockServer;
    TossPaymentHttpClientImpl tossPaymentHttpClient;

    @BeforeEach
    void setUp(){
        // wire mock 서버 시작
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        // 타임아웃을 위한 정책 설정
        HttpClient httpClient = HttpClientFactory.create(List.of(
                ConnectionPolicy.builder().socketTimeout(Timeout.ofMilliseconds(200))
                        .build()
        ));

        // 4XX, 5XX 응답 시, 처리를 위한 핸들러 설정
        TossPaymentClientErrorHandler tossPaymentClientErrorHandler = new TossPaymentClientErrorHandler();
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wireMockServer.port())
                .defaultStatusHandler(HttpStatusCode::isError, tossPaymentClientErrorHandler)
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();

        // 네트워크 관련 예외 발생 시, 서비스 내부 예외로 변환을 위한 번역기 설정
        TossPaymentNetworkExceptionTranslator tossNetworkExceptionTranslator = new TossPaymentNetworkExceptionTranslator();
        tossPaymentHttpClient = new TossPaymentHttpClientImpl(restClient, tossNetworkExceptionTranslator);
    }

    @AfterEach
    void tearDown(){
        wireMockServer.stop();
    }

    @Test
    void 정상적으로_결제가_승인된다(){
        // given
        TossPaymentApproveRequest request = new TossPaymentApproveRequest("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // wireMock가 정상 응답 반환하도록 설정
        wireMockServer.stubFor(post(urlEqualTo("/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                          "status" : "DONE",
                          "approvedAt": "2024-02-13T12:18:14+09:00",
                          "method": "카드"
                        }
                        """)
                ));

        //when
        TossPaymentApproveResponse response = tossPaymentHttpClient.callPayApprovalApi(request, UUID.randomUUID().toString());

        //then
        assertEquals("DONE", response.getStatus());
        assertEquals(OffsetDateTime.parse("2024-02-13T03:18:14Z"), response.getApprovedAt());
        assertEquals("카드", response.getMethod());
    }

    @Test
    void 클라이언트_에러_응답시_토스_클라이언트_예외가_발생한다(){

        // given
        TossPaymentApproveRequest request = new TossPaymentApproveRequest("a4CWyWY5m89PNh7xJwhk1",
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

        //when && then
        assertThrows(TossPaymentClientException.class,
                () -> tossPaymentHttpClient.callPayApprovalApi(request, UUID.randomUUID().toString()));
    }

    @Test
    void 서버_에러_응답시_토스_서버_예외가_throw된다(){
        // given
        TossPaymentApproveRequest request = new TossPaymentApproveRequest("a4CWyWY5m89PNh7xJwhk1",
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

        // when && then
        assertThrows(TossPaymentServerException.class,
                () -> tossPaymentHttpClient.callPayApprovalApi(request, UUID.randomUUID().toString()));
    }

    @Test
    void 타임아웃시_toss_타임아웃_예외가_throw된다(){
        // given
        TossPaymentApproveRequest request = new TossPaymentApproveRequest("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // wireMock가 시간안에 응답을 반환하지 않도록 설정
        wireMockServer.stubFor(
                post(urlEqualTo("/payments/confirm"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(1000) // read timeout 유도
                        )
        );

        //when && then
        assertThrows(TossPaymentTimeoutException.class, () -> tossPaymentHttpClient.callPayApprovalApi(request, UUID.randomUUID().toString()));
    }

}