package jongwon.e_commerce.order.repository.jpa;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    @Query("select oi from OrderItemEntity oi " +
            "join fetch oi.productEntity " +
            "where oi.orderEntity.id = :orderId")
    List<OrderItemEntity> findByOrderId(Long orderId);
}
