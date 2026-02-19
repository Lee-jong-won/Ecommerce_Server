package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.toss.TossPaymentGateWay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentCompleteServiceTest {
    @InjectMocks
    PaymentCompleteService paymentCompleteService;
    @Mock
    PaymentResultService paymentResultService;
    @Mock
    StockService stockService;
    @Mock
    TossPaymentGateWay tossPaymentGateWay;

    @Test
    void DB저장실패시_보상트랜잭션이_수행된다() {
        // given
        doThrow(new DataAccessException("fail") {})
                .when(paymentResultService)
                .applySuccess(any(), any(), any());

        // when
        paymentCompleteService.completeSuccess("paymentKey", "orderId", OffsetDateTime.now(), "CARD");

        // then
        verify(stockService).increaseStock("orderId");
        verify(tossPaymentGateWay).payCancel(any());
    }

}