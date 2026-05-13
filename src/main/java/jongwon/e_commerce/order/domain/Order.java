package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.exception.EmptyOrderItemsException;
import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import jongwon.e_commerce.order.exception.NotOrderOwnerException;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Order {

    private Long id;
    private String orderId;
    private Member member;
    private String orderName;
    private LocalDateTime orderedAt;
    private List<OrderItem> orderItems;
    private OrderStatus orderStatus;
    private long totalAmount;

    public void setTotalAmount(int totalAmount){
        this.totalAmount = totalAmount;
    }
    public void setOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }

    public static int calculateTotalAmount(List<OrderItem> orderItems){
        int sum = 0;
        for(OrderItem orderItem : orderItems){
            sum += orderItem.calculateAmount();
        }
        return sum;
    }

    public static Order createOrder(Member member,
                             LocalDateTime orderedAt,
                             String orderId,
                             List<OrderItem> orderItems,
                             String orderName){
        int totalAmount = calculateTotalAmount(orderItems);
        Order order = Order.builder().
                orderItems(orderItems).
                member(member).
                orderedAt(orderedAt).
                orderId(orderId).
                totalAmount(totalAmount).
                orderedAt(orderedAt).
                orderName(orderName).build();
        return order;
    }

    public static String createOrderId(){
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                .format(LocalDateTime.now());
        String random = UUID.randomUUID().toString().substring(0, 8);
        return "ORD-" + timestamp + "-" + random;
    }

    public void validatePayAmount(long payAmount){
        if(this.totalAmount != payAmount)
            throw new InvalidAmountException();
    }

    public void place(){
        validate();
        ordered();
    }

    private void validate(){
        if(orderItems.isEmpty()){
            throw new EmptyOrderItemsException("주문 항목이 비어있습니다.");
        }

        for(OrderItem orderItem : orderItems){
            orderItem.validate();
        }
    }

    private void ordered(){
        this.orderStatus = OrderStatus.ORDERED;
    }

    // 결제 전처리 성공 시
    public void paymentPending() {
        if(this.orderStatus != OrderStatus.ORDERED){
            throw new InvalidOrderStateException("주문 된 상태에서만, 결제 진행 중으로 처리 가능합니다.");
        }
        setOrderStatus(OrderStatus.PAYMENT_PENDING);
    }

    // 결제 실패 시
    public void fail(){
        if(this.orderStatus != OrderStatus.PAYMENT_PENDING){
            throw new InvalidOrderStateException("결제 진행 중 상태에서만, 실패 처리가 가능합니다.");
        }
        setOrderStatus(OrderStatus.FAIL);
    }

    // PG로부터 결제 승인 성공 응답을 받을 시
    public void paid() {
        if (this.orderStatus != OrderStatus.PAYMENT_PENDING) {
            throw new InvalidOrderStateException("결제 진행 중 상태에서만 결제 성공 처리 가능합니다.");
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
