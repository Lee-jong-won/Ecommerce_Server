package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import lombok.Getter;

import java.util.*;

@Getter
public class OrderItemMemoryRepository implements OrderItemRepository {

    private static Map<Long, OrderItemEntity> store = new HashMap<>();
    private static Long sequence = 0L;

    @Override
    public OrderItemEntity save(OrderItemEntity orderItemEntity) {
        orderItemEntity.setOrderItemId(++sequence);
        store.put(orderItemEntity.getOrderItemId(), orderItemEntity);
        return orderItemEntity;
    }

    @Override
    public List<OrderItemEntity> findByOrder(OrderEntity orderEntity) {
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        for(OrderItemEntity orderItemEntity : store.values()){
            if(orderItemEntity.getOrderEntity() == orderEntity)
                orderItemEntities.add(orderItemEntity);
        }
        return orderItemEntities;
    }

    @Override
    public Optional<OrderItemEntity> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void clearStore(){
        store.clear();
    }

}
