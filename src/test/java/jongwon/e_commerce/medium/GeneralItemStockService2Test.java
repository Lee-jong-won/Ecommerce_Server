package jongwon.e_commerce.medium;

import jongwon.e_commerce.product.application.GeneralItemStockService;
import jongwon.e_commerce.product.application.GeneralItemStockService2;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.fixture.ProductFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class GeneralItemStockService2Test {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    GeneralItemStockService2 generalItemStockService2;

    @Test
    void 재고가_성공적으로_감소한다(){
        // given
        Product product = ProductFixture.createLaptop();
        product.startSelling();
        Long productId = productRepository.save(product).getProductId();


        // when
        Product updatedProduct = generalItemStockService2.decreaseStock(productId, 2);

        // then
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(98);
    }

    @Test
    void 재고가_성공적으로_증가한다(){
        // given
        Product product = ProductFixture.createLaptop();
        product.startSelling();
        Long productId = productRepository.save(product).getProductId();

        // when
        Product updatedProduct = generalItemStockService2.increaseStock(productId, 2);

        // then
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(102);
    }

    @Test
    void 동시에_재고_차감시_동시성_문제_발생() throws InterruptedException {
        // given
        Product product = ProductFixture.createLaptop();
        product.startSelling();
        Long productId = productRepository.save(product).getProductId();

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    generalItemStockService2.decreaseStock(productId, 5);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();

        // then
        Product result = productRepository.findById(productId).orElseThrow();
        assertThat(result.getStockQuantity()).isEqualTo(50);
    }

}
