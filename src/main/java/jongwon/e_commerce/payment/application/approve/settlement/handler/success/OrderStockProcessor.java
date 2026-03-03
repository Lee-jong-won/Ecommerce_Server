package jongwon.e_commerce.payment.application.approve.settlement.handler.success;

import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStockProcessor {

    private final OrderItemRepository orderItemRepository;

    public void deductStockBy(Pay pay) {
        OrderEntity orderEntity = pay.getOrderEntity();
        List<OrderItemEntity> orderItemEntities = orderItemRepository.findByOrder(orderEntity);
        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            Product product = orderItemEntity.getProduct();
            product.removeStock(orderItemEntity.getOrderQuantity());
        }
    }

    public void restoreStockBy(Pay pay){
        OrderEntity orderEntity = pay.getOrderEntity();
        List<OrderItemEntity> orderItemEntities = orderItemRepository.findByOrder(orderEntity);
        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            Product product = orderItemEntity.getProduct();
            product.addStock(orderItemEntity.getOrderQuantity());
        }
    }
}
