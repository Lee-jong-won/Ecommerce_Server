package jongwon.e_commerce.payment.toss.gateway;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.external.TossPaymentClientException;
import jongwon.e_commerce.payment.exception.external.TossPaymentServerException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import jongwon.e_commerce.payment.toss.TossPaymentHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TossPaymentGateWayTest {
    TossPaymentHttpClient tossPaymentHttpClient;
    RetryTemplate retryTemplate;
    TossPaymentGateWay tossPaymentGateWay;

    @BeforeEach
    public void beforeEach(){
        tossPaymentHttpClient = mock(TossPaymentHttpClient.class);
        retryTemplate = new RetryTemplate();

        // 재시도 정책: 최대 3회
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(
                5,
                Map.of(
                        TossPaymentTimeoutException.class, true
                )
        );
        retryTemplate.setRetryPolicy(retryPolicy);

        // 고정 간격 200ms 대기
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(200);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        tossPaymentGateWay = new TossPaymentGateWay(tossPaymentHttpClient, retryTemplate);
    }

    @Test
    void 응답이_성공적으로_반환된다(){
        // given
        TossPaymentApproveRequest request = new TossPaymentApproveRequest("주문1", "1234", 10000);
        String idempotencyKey = "idempotency-key";
        TossPaymentApproveResponse response = new TossPaymentApproveResponse("paymentKey",
                "order1", "카드",
                OffsetDateTime.parse("2024-02-13T03:18:14Z"),
                "DONE", null);

        when(tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey)).thenReturn(
                response
        );

        // when
        TossPaymentApproveResponse result = tossPaymentGateWay.payApprove(request, idempotencyKey);

        // then
        assertEquals(response, result);
    }

    @Test
    void PG사로부터_클라이언트_오류_응답시_예외가_발생한다(){
        // given
        TossPaymentApproveRequest request = new TossPaymentApproveRequest("주문1", "1234", 10000);
        String idempotencyKey = "idempotency-key";
        when(tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey)).thenThrow(new TossPaymentClientException("클라이언트 오류입니다"));

        // when && then
        assertThrows(TossPaymentClientException.class,
                () -> tossPaymentGateWay.payApprove(request, idempotencyKey));
    }

    @Test
    void PG사로부터_서버_오류_응답시_예외가_발생한다(){
        // given
        TossPaymentApproveRequest request = new TossPaymentApproveRequest("주문1", "1234", 10000);
        String idempotencyKey = "idempotency-key";
        when(tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey)).thenThrow(new TossPaymentServerException("서버 오류입니다"));

        // when && then
        assertThrows(TossPaymentServerException.class,
                () -> tossPaymentGateWay.payApprove(request, idempotencyKey));
    }

    @Test
    void 타임아웃시_재시도_횟수만큼_재시도가_이루어진다(){
        // given
        TossPaymentApproveRequest request = new TossPaymentApproveRequest("주문1", "1234", 10000);
        String idempotencyKey = "idempotency-key";
        when(tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey)).thenThrow(new TossPaymentTimeoutException("타임아웃 발생!"));

        // when && then
        assertThrows(TossPaymentTimeoutException.class,
                () -> tossPaymentGateWay.payApprove(request, idempotencyKey));
        verify(tossPaymentHttpClient, times(5)).callPayApprovalApi(request, idempotencyKey);
    }

}