package jongwon.e_commerce.order.infra;

import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class OrderItemJpaRepositoryAdapter implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderItem save(Long orderId, Long productId, String productName, int productPrice, int stockQuantity) {
        OrderItem orderItem = OrderItem.createOrderItem(orderId, productId, productName, productPrice, stockQuantity);
        return orderItemJpaRepository.save(orderItem);
    }

}
