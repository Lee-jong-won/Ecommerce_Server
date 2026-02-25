package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import jongwon.e_commerce.product.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    public void 총_주문금액_계산(){
        // given
        List<OrderItem> orderItems = new ArrayList<>();
        for(int i = 1; i < 4; i++) {
            Product product = Product.createProduct("상품" + i, 1000);
            orderItems.add(OrderItem.createOrderItem(product, product.getProductName(),
                    product.getProductPrice(), 3));

        }
        Order order = new Order();

        // when
        order.calculateTotalAmount(orderItems);

        // then
        assertEquals(9000, order.getTotalAmount());
    }

    @Test
    public void 주문_생성(){
        // given
        Member member = Member.create("wwwl7749", "1234", "이종원", "dlwhddnjs951@korea.kr",
                "경기도 고양시");

        String orderName = "주문1";

        List<OrderItem> orderItems = new ArrayList<>();
        for(int i = 1; i < 4; i++) {
            Product product = Product.createProduct("상품" + i, 1000);
            orderItems.add(OrderItem.createOrderItem(product, product.getProductName(),
                    product.getProductPrice(), 3));

        }

        LocalDateTime orderedAt = LocalDateTime.now();

        // when
        Order order = Order.createOrder(member, orderName, orderedAt, orderItems);

        // then
        assertEquals(9000, order.getTotalAmount());
        assertEquals(member, order.getMember());
        assertEquals(orderName, order.getOrderName());
        assertEquals(OrderStatus.ORDERED, order.getOrderStatus());
        assertEquals(orderedAt, order.getOrderedAt());
    }

    @Test
    public void 결제중으로_주문상태_변경(){
        // given
        Order order = new Order();
        order.setOrderStatus(OrderStatus.ORDERED);

        // when
        order.markPaymentPending();

        // then
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
    }

    @Test
    public void 결제중으로_주문상태_변경_예외(){
        //given
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PAYMENT_FAILED);

        //when && then
        assertThrows(InvalidOrderStateException.class, () -> order.markPaymentPending());
    }

    @Test
    public void 결제완료로_주문상태_변경(){
        //given
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PAYMENT_PENDING);

        //when
        order.markPaid();

        //then
        assertEquals(OrderStatus.PAYMENT_SUCCESS, order.getOrderStatus());
    }

    @Test
    public void 결제완료로_주문상태_변경_예외(){
        //given
        Order order = new Order();

        //when
        order.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> order.markPaid());
    }

    @Test
    public void 결제실패로_주문상태_변경(){
        //given
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PAYMENT_PENDING);

        //when
        order.markFailed();

        //then
        assertEquals(OrderStatus.PAYMENT_FAILED, order.getOrderStatus());
    }

    @Test
    public void 결제실패로_주문상태_변경_예외(){
        //given
        Order order = new Order();

        //when
        order.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> order.markFailed());
    }

    @Test
    public void 주문취소로_주문상태_변경(){
        //given
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);

        //when
        order.markCancel();

        //then
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
    }

    @Test
    public void 주문취소로_주문상태_변경_예외(){
        //given
        Order order = new Order();

        //when
        order.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> order.markCancel());
    }

}