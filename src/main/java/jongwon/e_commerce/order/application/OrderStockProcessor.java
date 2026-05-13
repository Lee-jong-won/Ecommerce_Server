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

    private final List<StockService> stockServices;

    @Transactional
    public void deductStockOf(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        log.info("주문 상품 조회 완료");
        for (OrderItem orderItem : orderItems) {
            stockServices.stream()
                    .filter(h -> h.support(orderItem.getProduct()))
                    .findAny()
                    .ifPresent(h -> {
                        h.decreaseStock(orderItem.getProduct().getProductId(), orderItem.getOrderQuantity());
                    });
        }
    }

    @Transactional
    public void restoreStockOf(Order order){
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            stockServices.stream()
                    .filter(h -> h.support(orderItem.getProduct()))
                    .findAny()
                    .ifPresent(h -> {
                        Product increasedProduct = h.increaseStock(orderItem.getProduct().getProductId(), orderItem.getOrderQuantity());
                    });
        }
    }
}
