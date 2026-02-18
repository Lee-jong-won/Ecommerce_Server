package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OrderMemoryRepository implements OrderRepository{

    private static Map<Long, Order> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Order save(Long memberId, String orderName) {
        Order order = Order.createOrder(memberId, orderName);
        order.setOrderId(++sequence);
        store.put(order.getOrderId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Order> findByPayOrderId(String payOrderId) {
        Order findOrder = null;
        for(Order order : store.values()){
            if(order.getPayOrderId().equals(payOrderId)) {
                findOrder = order;
                break;
            }
        }
        return Optional.ofNullable(findOrder);
    }

    public void clearStore(){
        store.clear();
    }

}
