package jongwon.e_commerce.order.application;

import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderStockProcessor {
    public void deductStockBy(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.removeStock(orderItem.getOrderQuantity());
        }
    }

    public void restoreStockBy(List<OrderItem> orderItems){
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.addStock(orderItem.getOrderQuantity());
        }
    }
}
