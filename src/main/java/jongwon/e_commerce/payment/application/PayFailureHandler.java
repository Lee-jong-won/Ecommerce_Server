package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayFailureHandler {

    private final PayRequestRepository payRequestRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void processBusinessFailed(Long payRequestId) {
        PayRequest payRequest = getPayRequestById(payRequestId);
        Order order = getOrderById(payRequest.getOrderId());

        order.fail();
        payRequest.businessFailed();

        orderRepository.save(order);
        payRequestRepository.save(payRequest);
    }

    @Transactional
    public void processServerFailed(Long payRequestId){
        PayRequest payRequest = getPayRequestById(payRequestId);
        Order order = getOrderById(payRequest.getOrderId());

        order.fail();
        payRequest.serverFailed();

        orderRepository.save(order);
        payRequestRepository.save(payRequest);
    }

    @Transactional
    public void processPGFailed(Long payRequestId){
        PayRequest payRequest = getPayRequestById(payRequestId);
        Order order = getOrderById(payRequest.getOrderId());

        order.fail();
        payRequest.pgFailed();

        orderRepository.save(order);
        payRequestRepository.save(payRequest);
    }

    @Transactional
    public void processUnknownOutcome(Long payRequestId) {
        PayRequest payRequest = getPayRequestById(payRequestId);

        payRequest.unknown();
        payRequestRepository.save(payRequest);
    }

    private PayRequest getPayRequestById(Long id){
        return payRequestRepository.getById(id);
    }

    private Order getOrderById(Long id){
        return orderRepository.getById(id);
    }

}
