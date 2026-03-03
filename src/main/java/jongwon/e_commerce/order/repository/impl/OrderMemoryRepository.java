package jongwon.e_commerce.order.repository.impl;

import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.OrderRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OrderMemoryRepository implements OrderRepository {

    private static Map<Long, OrderEntity> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public OrderEntity save(OrderEntity orderEntity) {
        orderEntity.setOrderId(++sequence);
        store.put(orderEntity.getId(), orderEntity);
        return orderEntity;
    }

    @Override
    public Optional<OrderEntity> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<OrderEntity> findByOrderId(String orderId) {
        OrderEntity findOrderEntity = null;
        for(OrderEntity orderEntity : store.values()){
            if(orderEntity.getOrderId().equals(orderId)) {
                findOrderEntity = orderEntity;
                break;
            }
        }
        return Optional.ofNullable(findOrderEntity);
    }

    @Override
    public void clearStore(){
        store.clear();
    }

}
