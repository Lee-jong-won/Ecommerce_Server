package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private Long id;
    private String orderId;
    private Member member;
    private String orderName;
    private LocalDateTime orderedAt;
    private OrderStatus orderStatus;
    private long totalAmount;

    public void calculateTotalAmount(List<OrderItem> orderItems){
        int sum = 0;
        for(OrderItem orderItem : orderItems){
            sum += orderItem.calculateAmount();
        }
        setTotalAmount(sum);
    }

    public void setTotalAmount(int totalAmount){
        this.totalAmount = totalAmount;
    }
    public void setOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }

    public static Order from(Member member,
                             String orderName,
                             String orderId,
                             LocalDateTime orderedAt,
                             List<OrderItem> orderItems){
        Order order = new Order();
        order.member = member;
        order.orderId = orderId;
        order.calculateTotalAmount(orderItems);
        order.orderStatus = OrderStatus.ORDERED;
        order.orderedAt = orderedAt;
        order.orderName = orderName;
        return order;
    }

    //PG로부터 결제 승인 성공 응답을 받을 시
    public void paid() {
        if (this.orderStatus != OrderStatus.ORDERED) {
            throw new InvalidOrderStateException("주문 된 상태에서만 결제 성공 처리 가능합니다.");
        }
        setOrderStatus(OrderStatus.PAID);
    }

    //고객에 의해 주문이 취소 되었을 시
    public void markCancel(){
        if(this.orderStatus != OrderStatus.PAID){
            throw new InvalidOrderStateException("결제가 완료된 상태 에서만 취소 처리 가능합니다.");
        }
        setOrderStatus(OrderStatus.CANCEL);
    }

}
