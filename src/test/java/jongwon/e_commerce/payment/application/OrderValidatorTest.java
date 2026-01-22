package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderValidatorTest {
    @InjectMocks
    OrderValidator orderValidator;
    @Mock
    OrderRepository orderRepository;

    @Test
    void 주문이_존재하고_금액이_일치하면_Order를_반환한다() {
        // given
        Long orderId = 1L;
        Long amount = 10000L;

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        Order order = mock(Order.class);

        when(request.getOrderId()).thenReturn(orderId);
        when(request.getAmount()).thenReturn(amount);
        when(order.getTotalAmount()).thenReturn(amount);
        when(order.getOrderId()).thenReturn(orderId);
        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // when
        Order result = orderValidator.validateOrder(request);

        // then
        assertEquals(10000, result.getTotalAmount());
        assertEquals(1L, result.getOrderId());
    }

    @Test
    void 주문이_없으면_OrderNotExistException이_발생한다() {
        // given
        Long orderId = 1L;

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        when(request.getOrderId()).thenReturn(orderId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(
                OrderNotExistException.class,
                () -> orderValidator.validateOrder(request)
        );
    }

    @Test
    void 주문금액과_결제금액이_다르면_InvalidAmountException이_발생한다() {
        // given
        Long orderId = 1L;

        TossPaymentApproveRequest request = mock(TossPaymentApproveRequest.class);
        Order order = mock(Order.class);

        when(request.getOrderId()).thenReturn(orderId);
        when(request.getAmount()).thenReturn(10_000L);
        when(order.getTotalAmount()).thenReturn(9_000L);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // when & then
        assertThrows(
                InvalidAmountException.class,
                () -> orderValidator.validateOrder(request)
        );
    }
}