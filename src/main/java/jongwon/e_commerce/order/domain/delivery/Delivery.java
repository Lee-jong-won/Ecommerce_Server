package jongwon.e_commerce.order.domain.delivery;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "delivery")
public class Delivery {

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

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime updatedAt;

    public static Delivery createDelivery(Long orderId, String shipAddress){
        Delivery delivery = new Delivery();
        delivery.orderId = orderId;
        delivery.shipAddress = shipAddress;
        return delivery;
    }
}
