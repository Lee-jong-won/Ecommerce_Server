package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiTimeoutException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import jongwon.e_commerce.payment.infra.toss.TossPaymentClient;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentCancelRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentApprovalFacadeTest {
    @InjectMocks
    PaymentApprovalFacade facade;
    @Mock
    PaymentPrepareService paymentPrepareService;
    @Mock
    PaymentResultService paymentResultService;
    @Mock
    TossPaymentClient tossPaymentClient;
    @Mock
    RetryTemplate tossRetryTemplate;

    @Test
    void 결제승인_성공시_prepare_approve_applySuccess_호출된다() {
        // given
        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        TossPaymentApproveResponse response = mock(TossPaymentApproveResponse.class);
        when(request.getPaymentKey()).thenReturn("payKey");
        when(request.getPayOrderId()).thenReturn("abcd");
        when(tossRetryTemplate.execute(any())).thenReturn(response);

        // when
        TossPaymentApproveResponse result = facade.approvePayment(request);

        // then
        assertSame(response, result);
        verify(paymentPrepareService).preparePayment(request);
        verify(paymentResultService)
                .applySuccess("payKey", "abcd", response);
    }

    @Test
    void 결제승인_성공후_DB반영_실패시_결제취소API를_호출한다() {
        // given
        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        TossPaymentApproveResponse response = mock(TossPaymentApproveResponse.class);
        when(request.getPaymentKey()).thenReturn("payKey");
        when(request.getPayOrderId()).thenReturn("abcd");

        when(tossRetryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback<?, ?> callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });

        when(tossPaymentClient.approve(any()))
                .thenReturn(response);

        doThrow(new DataAccessException("DB FAIL") {})
                .when(paymentResultService)
                .applySuccess(any(), any(), any());

        // when
        facade.approvePayment(request);

        // then
        verify(tossPaymentClient)
                .cancel(any(TossPaymentCancelRequest.class));
    }

    @Test
    void 사용자오류면_applyFail_호출되고_예외를_다시던진다(){
        // given
        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getPaymentKey()).thenReturn("payKey");
        when(request.getPayOrderId()).thenReturn("abcd");
        TossPaymentUserFaultException ex = new TossPaymentUserFaultException(PaymentErrorCode.INVALID_REQUEST);
        when(tossRetryTemplate.execute(any())).thenThrow(ex);

        // when & then
        assertThrows(
                TossPaymentUserFaultException.class,
                () -> facade.approvePayment(request)
        );

        verify(paymentPrepareService).preparePayment(request);
        verify(paymentResultService)
                .applyFail("payKey", "abcd");
    }

    @Test
    void 타임아웃으로_재시도_실패시_applyTimeout_호출되고_예외를_다시던진다() {
        // given
        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getPaymentKey()).thenReturn("payKey");

        TossApiTimeoutException ex =
                new TossApiTimeoutException(PaymentErrorCode.TOSS_API_TIMEOUT_ERROR);

        when(tossRetryTemplate.execute(any())).thenThrow(ex);

        // when & then
        assertThrows(
                TossApiTimeoutException.class,
                () -> facade.approvePayment(request)
        );

        verify(paymentPrepareService).preparePayment(request);
        verify(paymentResultService)
                .applyTimeout("payKey");
    }

    @Test
    void 타임아웃_아닌_오류로_재시도_실패시_applyFailure가_호출되고_예외를_다시던진다() {
        // given
        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getPaymentKey()).thenReturn("payKey");
        when(request.getPayOrderId()).thenReturn("abcd");

        TossPaymentRetryableException ex =
                new TossPaymentRetryableException(PaymentErrorCode.PROVIDER_ERROR);

        when(tossRetryTemplate.execute(any())).thenThrow(ex);

        // when & then
        assertThrows(
                TossPaymentRetryableException.class,
                () -> facade.approvePayment(request)
        );

        verify(paymentPrepareService).preparePayment(request);
        verify(paymentResultService)
                .applyFail("payKey", "abcd");
    }
}