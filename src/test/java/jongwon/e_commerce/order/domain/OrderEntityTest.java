package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;
import jongwon.e_commerce.product.domain.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {

    @Test
    public void 총_주문금액_계산(){
        // given
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        for(int i = 1; i < 4; i++) {
            Product product = Product.createProduct("상품" + i, 1000);
            orderItemEntities.add(OrderItemEntity.createOrderItem(product, product.getProductName(),
                    product.getProductPrice(), 3));

        }
        OrderEntity orderEntity = new OrderEntity();

        // when
        orderEntity.calculateTotalAmount(orderItemEntities);

        // then
        assertEquals(9000, orderEntity.getTotalAmount());
    }

    @Test
    public void 주문_생성(){
        // given
        Member member = Member.create("wwwl7749", "1234", "이종원", "dlwhddnjs951@korea.kr",
                "경기도 고양시");

        String orderName = "주문1";

        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        for(int i = 1; i < 4; i++) {
            Product product = Product.createProduct("상품" + i, 1000);
            orderItemEntities.add(OrderItemEntity.createOrderItem(product, product.getProductName(),
                    product.getProductPrice(), 3));

        }

        LocalDateTime orderedAt = LocalDateTime.now();

        // when
        OrderEntity orderEntity = OrderEntity.createOrder(member, orderName, orderedAt, orderItemEntities);

        // then
        assertEquals(9000, orderEntity.getTotalAmount());
        assertEquals(member, orderEntity.getMember());
        assertEquals(orderName, orderEntity.getOrderName());
        assertEquals(OrderStatus.ORDERED, orderEntity.getOrderStatus());
        assertEquals(orderedAt, orderEntity.getOrderedAt());
    }

    @Test
    public void 결제중으로_주문상태_변경(){
        // given
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderStatus(OrderStatus.ORDERED);

        // when
        orderEntity.markPaymentPending();

        // then
        assertEquals(OrderStatus.PAYMENT_PENDING, orderEntity.getOrderStatus());
    }

    @Test
    public void 결제중으로_주문상태_변경_예외(){
        //given
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderStatus(OrderStatus.PAYMENT_FAILED);

        //when && then
        assertThrows(InvalidOrderStateException.class, () -> orderEntity.markPaymentPending());
    }

    @Test
    public void 결제완료로_주문상태_변경(){
        //given
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderStatus(OrderStatus.PAYMENT_PENDING);

        //when
        orderEntity.markPaid();

        //then
        assertEquals(OrderStatus.PAYMENT_SUCCESS, orderEntity.getOrderStatus());
    }

    @Test
    public void 결제완료로_주문상태_변경_예외(){
        //given
        OrderEntity orderEntity = new OrderEntity();

        //when
        orderEntity.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> orderEntity.markPaid());
    }

    @Test
    public void 결제실패로_주문상태_변경(){
        //given
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderStatus(OrderStatus.PAYMENT_PENDING);

        //when
        orderEntity.markFailed();

        //then
        assertEquals(OrderStatus.PAYMENT_FAILED, orderEntity.getOrderStatus());
    }

    @Test
    public void 결제실패로_주문상태_변경_예외(){
        //given
        OrderEntity orderEntity = new OrderEntity();

        //when
        orderEntity.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> orderEntity.markFailed());
    }

    @Test
    public void 주문취소로_주문상태_변경(){
        //given
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);

        //when
        orderEntity.markCancel();

        //then
        assertEquals(OrderStatus.CANCELLED, orderEntity.getOrderStatus());
    }

    @Test
    public void 주문취소로_주문상태_변경_예외(){
        //given
        OrderEntity orderEntity = new OrderEntity();

        //when
        orderEntity.setOrderStatus(OrderStatus.CANCELLED);

        //then
        assertThrows(InvalidOrderStateException.class, () -> orderEntity.markCancel());
    }

}