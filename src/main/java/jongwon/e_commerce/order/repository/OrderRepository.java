package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.Order;

import java.util.Optional;

public interface OrderRepository {
    Order save(Long memberId, String orderName);
    Order findById(Long id);
}
