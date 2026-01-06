package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    public void 총_주문금액_계산(){
        List<OrderItem> orderItems = new ArrayList<>();
        for(int i = 1; i < 4; i++)
            orderItems.add(OrderItem.createOrderItem(1L, "상품" + i, i * 1000, 10));

        Order order = new Order();
        order.setTotalAmount(orderItems);

        assertEquals(6000, order.getTotalAmount());
    }

    @Test
    public void 결제중으로_주문상태_변경(){
        //given
        Order order = Order.createOrder(1L);

        //when
        order.markPaymentPending();

        //then
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
    }

    @Test
    public void 결제중으로_주문상태_변경_예외(){
        //given
        Order order = Order.createOrder(1L);

        //then
        order.setOrderStatus(OrderStatus.FAILED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> order.markPaymentPending());
    }

    @Test
    public void 결제완료로_주문상태_변경(){
        //given
        Order order = Order.createOrder(1L);
        order.setOrderStatus(OrderStatus.PAYMENT_PENDING);

        //when
        order.markPaid();

        //then
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
    }

    @Test
    public void 결제완료로_주문상태_변경_예외(){
        //given
        Order order = Order.createOrder(1L);

        //when
        order.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> order.markPaid());
    }

    @Test
    public void 결제실패로_주문상태_변경(){
        //given
        Order order = Order.createOrder(1L);
        order.setOrderStatus(OrderStatus.PAYMENT_PENDING);

        //when
        order.markFailed();

        //then
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
    }

    @Test
    public void 결제실패로_주문상태_변경_예외(){
        //given
        Order order = Order.createOrder(1L);

        //when
        order.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> order.markFailed());
    }

    @Test
    public void 주문취소로_주문상태_변경(){
        //given
        Order order = Order.createOrder(1L);
        order.setOrderStatus(OrderStatus.PAYMENT_PENDING);

        //when
        order.markCancel();

        //then
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
    }

    @Test
    public void 주문취소로_주문상태_변경_예외(){
        //given
        Order order = Order.createOrder(1L);

        //when
        order.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> order.markCancel());
    }

}