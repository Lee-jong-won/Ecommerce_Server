package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.OrderItem;
import lombok.Getter;

import java.util.*;

@Getter
public class OrderItemMemoryRepository implements OrderItemRepository {

    private static Map<Long, OrderItem> store = new HashMap<>();
    private static Long sequence = 0L;

    @Override
    public OrderItem save(Long orderId, Long productId, String productName, int orderPrice, int orderQuantity) {
        OrderItem orderItem = OrderItem.createOrderItem(orderId, productId, productName, orderPrice, orderQuantity);
        orderItem.setOrderItemId(++sequence);
        store.put(orderItem.getOrderItemId(), orderItem);
        return orderItem;
    }

    @Override
    public List<OrderItem> findOrderItems(Long orderId) {
        List<OrderItem> findOrderItemList = new ArrayList<>();
        for(OrderItem orderItem : store.values()){
            if(orderItem.getOrderId() == orderId)
                findOrderItemList.add(orderItem);
        }
        return findOrderItemList;
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void clearStore(){
        store.clear();
    }

}
