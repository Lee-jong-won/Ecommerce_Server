package jongwon.e_commerce.support.scenario;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.controller.Cart;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.fixture.*;

import java.util.List;

public class TestDataFactory {

    // 영속화 메소드
    public static PrepareOrderData prepareOrder(MemberRepository memberRepository,
                                                ProductRepository productRepository){
        Member member = memberRepository.save(MemberFixture.builder().build().create());
        Product product1 = productRepository.save(ProductFixture.
                builder().
                productName("상품1").
                productStatus(ProductStatus.SELLING).
                productPrice(10000).
                build().
                create());
        Product product2 = productRepository.save(ProductFixture.
                builder().
                productName("상품2").
                productStatus(ProductStatus.SELLING).
                productPrice(5000).
                build().
                create());
        Cart.CartLineItem cartLineItem1 =  Cart.CartLineItem.builder().
                productId(product1.getProductId()).
                stockQuantity(1).
                build();
        Cart.CartLineItem cartLineItem2 = Cart.CartLineItem.builder().
                productId(product2.getProductId()).
                stockQuantity(1).
                build();
        return new PrepareOrderData(member, List.of(cartLineItem1, cartLineItem2));
    }

    public static FinishOrderData finishOrder(MemberRepository memberRepository,
                                    ProductRepository productRepository,
                                    OrderRepository orderRepository){
        Member member = memberRepository.save(MemberFixture.builder().build().create());
        Product product1 = productRepository.save(ProductFixture.
                builder().
                productName("상품1").
                productStatus(ProductStatus.SELLING).
                productPrice(10000).
                build().
                create());
        Product product2 = productRepository.save(ProductFixture.
                builder().
                productName("상품2").
                productStatus(ProductStatus.SELLING).
                productPrice(5000).
                build().
                create());

        OrderItem orderItem1 = OrderItemFixture.
                builder().
                product(product1).
                build().
                create();

        OrderItem orderItem2 = OrderItemFixture.
                builder().
                product(product2).
                build().create();

        List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

        Order order = orderRepository.save(OrderFixture.
                builder().
                member(member).
                totalAmount(15000).
                orderItems(orderItems).
                build().
                create());

        return new FinishOrderData(member, order);
    }

    public static PayRequest finishPayPreProcess(MemberRepository memberRepository,
                                                 ProductRepository productRepository,
                                                 OrderRepository orderRepository,
                                                 PayRequestRepository payRequestRepository){
        FinishOrderData finishOrderData = finishOrder(
                memberRepository,
                productRepository,
                orderRepository);

        Order order = finishOrderData.getOrder();
        order.paymentPending();
        orderRepository.save(order);

        PayRequest payRequest = payRequestRepository.save(PayRequestFixture.
                builder().
                order(finishOrderData.getOrder()).
                payAmount(finishOrderData.getOrder().getTotalAmount()).
                build().
                create());

        return payRequest;
    }
}
