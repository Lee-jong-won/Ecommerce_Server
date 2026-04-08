package jongwon.e_commerce.common.commandLineRunner;

import jakarta.persistence.EntityManager;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderExecutor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class TestDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderExecutor orderExecutor;
    private final EntityManager em;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        try {
            for (int i = 1; i <= 50000; i++) {
                memberRepository.save(Member.createMember(MemberCreate.builder().
                        memberName("user" + i).
                        loginId("user-" + i).
                        password("pass").
                        email("user" + i + "@test.com").
                        addr("Seoul").build()));


                if (i > 0 && i % 1000 == 0) {
                    em.flush();
                    em.clear();
                }
            }
            log.info("member 삽입 완료!");

            for (int i = 1; i <= 50000; i++) {
                Product product = Product.from("product-" + i, 1000);
                product.changeStock(30000);
                product.startSelling();
                productRepository.save(product);

                if (i > 0 && i % 1000 == 0) {
                    em.flush();
                    em.clear();
                }
            }
            log.info("product 삽입 완료!");


            for (long i = 1; i <= 50000L; i++) {
                Member member = memberRepository.getById(i);
                List<OrderItemCreate> orderItemCreates = List.of(new OrderItemCreate(i, 1));
                orderExecutor.order(member, "orderName-" + i, "orderId-" + i, orderItemCreates);

                if (i > 0 && i % 1000 == 0) {
                    em.flush();
                    em.clear();
                }
            }
            log.info("order 삽입 완료!");

        } finally {
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        }
    }
}
