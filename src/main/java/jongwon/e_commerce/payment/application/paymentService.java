package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.infra.PaymentRepository;
import jongwon.e_commerce.payment.infra.toss.TossPaymentClient;
import jongwon.e_commerce.payment.presentation.dto.PaymentApproveRequest;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.infra.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class paymentService {

    private final OrderValidator orderValidator;
    private final StockService stockService;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void preparePayment(PaymentApproveRequest request){
        // 1. 주문 검증
        Order order = orderValidator.validateOrder(request);

        // 2. 재고 차감
        stockService.decreaseStock(order.getOrderId());

        // 3. Payment 생성
        Pay payment = Pay.create(
                request.getOrderId(),
                request.getPayOrderId(),
                request.getOrderName(),
                request.getPaymentKey(),
                request.getAmount()
        );

        paymentRepository.save(payment);
    }
    
}
