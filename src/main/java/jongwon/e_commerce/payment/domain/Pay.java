package jongwon.e_commerce.payment.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pay")
public class Pay {
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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
