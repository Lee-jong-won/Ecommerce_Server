package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiTimeoutException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    RetryTemplate tossRetryTemplate;

    @Test
    void 결제승인_성공시_prepare_approve_applySuccess_호출된다() {
        // given
        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        TossPaymentApproveResponse response = mock(TossPaymentApproveResponse.class);
        when(request.getPaymentKey()).thenReturn("payKey");
        when(request.getOrderId()).thenReturn(10L);
        when(tossRetryTemplate.execute(any())).thenReturn(response);

        // when
        TossPaymentApproveResponse result = facade.approvePayment(request);

        // then
        assertSame(response, result);
        verify(paymentPrepareService).preparePayment(request);
        verify(paymentResultService)
                .applySuccess("payKey", 10L, response);
    }

    @Test
    void 사용자오류면_applyFail_호출되고_예외를_다시던진다(){
        // given
        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getPaymentKey()).thenReturn("payKey");
        when(request.getOrderId()).thenReturn(10L);
        TossPaymentUserFaultException ex = new TossPaymentUserFaultException(PaymentErrorCode.INVALID_REQUEST);
        when(tossRetryTemplate.execute(any())).thenThrow(ex);

        // when & then
        assertThrows(
                TossPaymentUserFaultException.class,
                () -> facade.approvePayment(request)
        );

        verify(paymentResultService)
                .applyFail("payKey", 10L);
    }

    @Test
    void 타임아웃이면_applyTimeout_호출되고_예외를_다시던진다() {
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

        verify(paymentResultService)
                .applyTimeout("payKey");
    }

    @Test
    void 재시도가능오류지만_타임아웃아니면_아무상태변경없다() {
        // given
        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);

        TossPaymentRetryableException ex =
                new TossPaymentRetryableException(PaymentErrorCode.PROVIDER_ERROR);

        when(tossRetryTemplate.execute(any())).thenThrow(ex);

        // when & then
        assertThrows(
                TossPaymentRetryableException.class,
                () -> facade.approvePayment(request)
        );

        verifyNoInteractions(paymentResultService);
    }
}