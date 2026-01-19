package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.infra.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StockService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public StockService(
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository
    ) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

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
                        new IllegalStateException("상품 정보가 존재하지 않습니다.")
                );

        product.removeStock(orderItem.getOrderQuantity());
    }
}
