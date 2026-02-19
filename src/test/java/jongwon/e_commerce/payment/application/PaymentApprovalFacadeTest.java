package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.external.TossPaymentServerException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import jongwon.e_commerce.payment.toss.TossPaymentGateWay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentApprovalFacadeTest {
    @InjectMocks
    PaymentApprovalFacade paymentApprovalFacade;
    @Mock
    TossPaymentGateWay tossPaymentGateWay;
    @Mock
    PreparePaymentApprovalService preparePaymentApprovalService;
    @Mock
    PaymentCompleteService paymentCompleteService;

    @Test
    void 정상승인시_completeSuccess가_호출된다(){
        // given
        TossPaymentApproveRequest request =
                new TossPaymentApproveRequest("orderId", "paymentKey", 1000L);

        TossPaymentApproveResponse response =
                new TossPaymentApproveResponse("CARD", OffsetDateTime.now(), "DONE");

        when(tossPaymentGateWay.payApprove(any(), any()))
                .thenReturn(response);

        // when
        paymentApprovalFacade.approvePayment(request);

        // then
        verify(preparePaymentApprovalService)
                .preparePaymentApproval(eq("orderId"), eq(1000L));
        verify(paymentCompleteService)
                .completeSuccess(eq("paymentKey"), eq("orderId"),
                        any(), eq("CARD"));
        verify(paymentCompleteService, never()).completeTimeout(any());
        verify(paymentCompleteService, never()).completeFail(any());
    }

    @Test
    void api호출후_타임아웃_예외_발생시_completeTimeout이_호출된다(){
        // given
        TossPaymentApproveRequest request =
                new TossPaymentApproveRequest("orderId", "paymentKey", 1000L);

        TossPaymentApproveResponse response =
                new TossPaymentApproveResponse("CARD", OffsetDateTime.now(), "DONE");

        when(tossPaymentGateWay.payApprove(any(), any()))
                .thenThrow(new TossPaymentTimeoutException("타임아웃 발생!"));

        // when
        paymentApprovalFacade.approvePayment(request);

        // then
        verify(paymentCompleteService)
                .completeTimeout(eq("orderId"));
        verify(paymentCompleteService, never()).completeSuccess(any(), any(), any(), any());
        verify(paymentCompleteService, never()).completeFail(any());
    }

    @Test
    void api호출후_실패응답으로_인한_예외_발생시_completeFail이_호출된다(){
        // given
        TossPaymentApproveRequest request =
                new TossPaymentApproveRequest("orderId", "paymentKey", 1000L);

        TossPaymentApproveResponse response =
                new TossPaymentApproveResponse("CARD", OffsetDateTime.now(), "DONE");

        when(tossPaymentGateWay.payApprove(any(), any()))
                .thenThrow(new TossPaymentServerException("서버 예외 발생!"));

        // when
        paymentApprovalFacade.approvePayment(request);

        // then
        verify(paymentCompleteService).completeFail(eq("orderId"));
        verify(paymentCompleteService, never()).completeSuccess(any(), any(), any(), any());
        verify(paymentCompleteService, never())
                .completeTimeout(any());
    }
}