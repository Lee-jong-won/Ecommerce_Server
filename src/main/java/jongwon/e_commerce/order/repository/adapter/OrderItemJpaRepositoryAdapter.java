package jongwon.e_commerce.order.repository.adapter;

import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@RequiredArgsConstructor
public class OrderItemJpaRepositoryAdapter implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;
    @Override
    public OrderItem save(Long orderId, Long productId,
                          String productName, int orderPrice, int orderQuantity) {
        OrderItem orderItem = OrderItem.createOrderItem(orderId, productId, productName, orderPrice, orderQuantity);
        return orderItemJpaRepository.save(orderItem);
    }
}
