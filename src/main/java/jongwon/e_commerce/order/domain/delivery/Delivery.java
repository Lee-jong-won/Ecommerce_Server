package jongwon.e_commerce.order.domain.delivery;

import jakarta.persistence.*;
import jongwon.e_commerce.common.domain.BaseEntity;
import jongwon.e_commerce.order.exception.InvalidDeliveryStateException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "delivery")
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @Column(
            name = "fk_order_id",
            nullable = false
    )
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "delivery_status",
            nullable = false,
            length = 20
    )
    private DeliveryStatus deliveryStatus;

    @Column(
            name = "tracking_no",
            length = 50
    )
    private String trackingNo;

    @Column(
            name = "ship_addr",
            nullable = false,
            length = 255
    )
    private String shipAddress;

    public static Delivery createDelivery(Long orderId, String shipAddress){
        Delivery delivery = new Delivery();
        delivery.orderId = orderId;
        delivery.deliveryStatus = DeliveryStatus.READY;
        delivery.shipAddress = shipAddress;
        return delivery;
    }

    //비즈니스 로직
    public void setDeliveryStatus(DeliveryStatus deliveryStatus){
        this.deliveryStatus = deliveryStatus;
    }

    public void startDelivery(String trackingNo) {
        if (this.deliveryStatus != DeliveryStatus.READY) {
            throw new InvalidDeliveryStateException("READY 상태에서만 배송을 시작할 수 있습니다.");
        }
        setDeliveryStatus(DeliveryStatus.SHIPPED);
        this.trackingNo = trackingNo;
    }

    public void completeDelivery() {
        if (this.deliveryStatus != DeliveryStatus.SHIPPED) {
            throw new InvalidDeliveryStateException("배송 중인 경우에만 완료할 수 있습니다.");
        }
        setDeliveryStatus(DeliveryStatus.DELIVERED);
    }
}
