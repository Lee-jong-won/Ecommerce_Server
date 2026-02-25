package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.Pay;

import java.util.Optional;

public interface PaymentRepository {
    void save(Pay pay);
}
