package jongwon.e_commerce.order.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.product.application.StockService;
import jongwon.e_commerce.product.domain.Product;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Builder
@Slf4j
public class OrderStockProcessor {

    private final OrderItemRepository orderItemRepository;
    private final List<StockService> stockServices;

    @Transactional
    public List<Product> deductStockOf(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        log.info("주문 상품 조회 완료");
        List<Product> deductedProducts = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            stockServices.stream()
                    .filter(h -> h.support(orderItem.getProduct()))
                    .findAny()
                    .ifPresent(h -> {
                        Product deductedProduct = h.decreaseStock(orderItem.getProduct().getProductId(), orderItem.getOrderQuantity());
                        deductedProducts.add(deductedProduct);
                    });
        }
        return deductedProducts;
    }

    @Transactional
    public List<Product> restoreStockOf(Long orderId){
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        List<Product> increasedProducts = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            stockServices.stream()
                    .filter(h -> h.support(orderItem.getProduct()))
                    .findAny()
                    .ifPresent(h -> {
                        Product increasedProduct = h.increaseStock(orderItem.getProduct().getProductId(), orderItem.getOrderQuantity());
                        increasedProducts.add(increasedProduct);
                    });
        }
        return increasedProducts;
    }
}
