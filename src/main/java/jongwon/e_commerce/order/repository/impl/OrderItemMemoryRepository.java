package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import lombok.Getter;

import java.util.*;

@Getter
public class OrderItemMemoryRepository implements OrderItemRepository {

    private static Map<Long, OrderItem> store = new HashMap<>();
    private static Long sequence = 0L;

    @Override
    public OrderItem save(OrderItem orderItem) {
        orderItem.setOrderItemId(++sequence);
        store.put(orderItem.getOrderItemId(), orderItem);
        return orderItem;
    }

    @Override
    public List<OrderItem> findByOrder(Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItem orderItem : store.values()){
            if(orderItem.getOrder() == order)
                orderItems.add(orderItem);
        }
        return orderItems;
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void clearStore(){
        store.clear();
    }

}
