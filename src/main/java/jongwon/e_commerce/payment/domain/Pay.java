package jongwon.e_commerce.payment.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.common.domain.BaseEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "pay")
public class Pay extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payId;

    @Column(name = "fk_order_id", nullable = false)
    private Long orderId;

    @Column(name = "pay_method", nullable = false, length = 50)
    private String payMethod;

    @Column(name = "pay_amount", nullable = false)
    private int payAmount;

    @Column(name = "pay_status", nullable = false, length = 20)
    private String payStatus;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
