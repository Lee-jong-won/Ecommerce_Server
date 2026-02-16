package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final OrderItemJpaRepository orderItemJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public void decreaseStock(Long orderId) {
        List<OrderItem> orderItems =
                orderItemJpaRepository.findByOrderId(orderId);

        for (OrderItem orderItem : orderItems) {
            decreaseSingleProductStock(orderItem);
        }
    }

    private void decreaseSingleProductStock(OrderItem orderItem) {
        Product product = productJpaRepository.findById(orderItem.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(orderItem.getProductId())
                );
        product.removeStock(orderItem.getOrderQuantity());
    }

}
