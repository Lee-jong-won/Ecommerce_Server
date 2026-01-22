package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.infra.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public void decreaseStock(Long orderId) {
        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);

        for (OrderItem orderItem : orderItems) {
            decreaseSingleProductStock(orderItem);
        }
    }

    private void decreaseSingleProductStock(OrderItem orderItem) {
        Product product = productRepository.findById(orderItem.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(orderItem.getProductId())
                );
        product.removeStock(orderItem.getOrderQuantity());
    }

}
