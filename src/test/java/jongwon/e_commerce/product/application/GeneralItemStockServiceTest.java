package jongwon.e_commerce.product.application;

import jongwon.e_commerce.mock.fake.FakeProductRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.infrastructure.config.RetryConfig;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.fixture.ProductFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


class GeneralItemStockServiceTest {

    ProductRepository productRepository;
    GeneralItemStockService generalItemStockService;

    @BeforeEach
    void init(){
        productRepository = new FakeProductRepository();
        GeneralItemStockServiceTx generalItemStockServiceTx = GeneralItemStockServiceTx.builder().
                productRepository(productRepository).
                build();
        RetryConfig retryConfig = new RetryConfig();
        generalItemStockService = GeneralItemStockService.builder().
                generalItemStockServiceTx(generalItemStockServiceTx).
                retryOperations(retryConfig.retryTemplate()).
                build();
    }

    @Test
    void 재고가_성공적으로_감소한다(){
        // given
        Product product = productRepository.save(ProductFixture.createLaptop());
        product.startSelling();

        // when
        Product updatedProduct = generalItemStockService.decreaseStock(product.getProductId(), 2);

        // then
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(98);
    }

    @Test
    void 재고가_성공적으로_증가한다(){
        // given
        Product product = productRepository.save(ProductFixture.createLaptop());
        product.startSelling();

        // when
        Product updatedProduct = generalItemStockService.increaseStock(product.getProductId(), 2);

        // then
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(102);
    }

}