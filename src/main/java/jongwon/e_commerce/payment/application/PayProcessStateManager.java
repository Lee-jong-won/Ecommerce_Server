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
public class PayProcessStateManager {

    private final PayRequestRepository payRequestRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void processBusinessFailed(PayRequest payRequest) {
        Order order = payRequest.getOrder();
        order.fail();
        payRequest.businessFailed();

        orderRepository.save(order);
        payRequestRepository.save(payRequest);
    }

    @Transactional
    public void processServerFailed(PayRequest payRequest){
        Order order = payRequest.getOrder();
        order.fail();
        payRequest.serverFailed();

        orderRepository.save(order);
        payRequestRepository.save(payRequest);
    }

    @Transactional
    public void processUnknownOutcome(PayRequest payRequest) {
        payRequest.unknown();
        payRequestRepository.save(payRequest);
    }

    @Transactional
    public void processRefund(PayRequest payRequest){
        Order order = payRequest.getOrder();
        order.markCancel();
        payRequest.refund();

        orderRepository.save(order);
        payRequestRepository.save(payRequest);
    }

}
