package jongwon.e_commerce.payment.application.approve.result;

import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.payment.application.approve.result.persistence.PaymentRecorder;
import jongwon.e_commerce.payment.domain.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletionProcessor {

    private final OrderStockProcessor orderStockProcessor;
    private final PaymentRecorder paymentRecorder;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void complete(Pay pay){
        //1. 재고 감소
        Order order = pay.getOrder();
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        orderStockProcessor.deductStockBy(orderItems);
        //2. 결제 데이터 생성 후 저장
        paymentRecorder.record(pay);
    }
}
