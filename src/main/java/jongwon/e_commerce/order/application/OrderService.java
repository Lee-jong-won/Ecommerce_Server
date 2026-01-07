package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.exception.MemberNotFoundException;
import jongwon.e_commerce.member.infra.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.delivery.Delivery;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.order.infra.DeliveryRepository;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.infra.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public Long order(Long memberId, List<OrderItemRequest> orderItemRequests){
        //Order를 DB에 저장
        Order order = Order.createOrder(memberId);
        orderRepository.save(order);

        //OrderItem DB에 저장
        for(OrderItemRequest orderItemRequest : orderItemRequests){
            Product product = productRepository.findById(orderItemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(orderItemRequest.getProductId()));

            OrderItem orderItem = OrderItem.createOrderItem(order.getOrderId(), product.getProductId(), product.getProductName(),
                    product.getProductPrice(), orderItemRequest.getStockQuantity());
            
            orderItemRepository.save(orderItem);
        }

        //주문 총액을 Order에 저장
        

        //Delivery를 DB에 저장
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Delivery delivery = Delivery.createDelivery(order.getOrderId(), member.getAddr());
        deliveryRepository.save(delivery);

        return order.getOrderId();
    }

}
