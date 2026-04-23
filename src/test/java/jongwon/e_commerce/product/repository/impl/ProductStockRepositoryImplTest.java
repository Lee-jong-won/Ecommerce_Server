package jongwon.e_commerce.product.repository.impl;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.infrastructure.jpa.ProductEntity;
import jongwon.e_commerce.product.infrastructure.jpa.ProductJpaRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.product.repository.ProductStockRepository;
import jongwon.e_commerce.support.fixture.ProductFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductStockRepositoryImplTest {

    @Autowired
    ProductStockRepositoryImpl productStockRepositoryImpl;

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Test
    void findBy_Id로_상품을_찾아올_수_있다(){
        // given
        Product product = ProductFixture.builder().build().create();
        ProductEntity productEntity = productJpaRepository.save(ProductEntity.from(product));

        // when
        Optional<Product> findProduct = productStockRepositoryImpl.findById(productEntity.getProductId());

        // then
        assertThat(findProduct.isEmpty()).isFalse();
    }

    @Test
    void getBy_Id로_상품을_찾아올_수_있다(){
        // given
        Product product = ProductFixture.builder().build().create();
        ProductEntity productEntity = productJpaRepository.save(ProductEntity.from(product));

        // when
        Product findProduct = productStockRepositoryImpl.getById(productEntity.getProductId());

        // then
        assertThat(findProduct.getProductId()).isEqualTo(productEntity.getProductId());
        assertThat(findProduct.getStockQuantity()).isEqualTo(productEntity.getStockQuantity());
        assertThat(findProduct.getProductStatus()).isEqualTo(productEntity.getProductStatus());
        assertThat(findProduct.getVersion()).isEqualTo(productEntity.getVersion());
    }

    @Test
    void 상품_재고를_업데이트_할_수_있다(){
        // given
        ProductEntity productEntity = productJpaRepository.save(ProductEntity.from(ProductFixture.
                builder().
                productStatus(ProductStatus.SELLING).
                build().
                create()));
        Product product = productStockRepositoryImpl.getById(productEntity.getProductId());
        product.removeStock(10);

        // when
        int success = productStockRepositoryImpl.updateStockAndStatus(
                product.getProductId(),
                product.getStockQuantity(),
                product.getProductStatus(),
                product.getVersion());

        // then
        Product result = productStockRepositoryImpl.getById(productEntity.getProductId());
        assertThat(success).isEqualTo(1);
        assertThat(result.getProductStatus()).isEqualTo(ProductStatus.SELLING);
        assertThat(result.getVersion()).isEqualTo(product.getVersion() + 1);
        assertThat(result.getStockQuantity()).isEqualTo(90);
    }

    @Test
    void 상품_재고와_상태을_모두_업데이트_할_수_있다(){
        // given
        ProductEntity productEntity = productJpaRepository.save(ProductEntity.from(ProductFixture.
                builder().
                productStatus(ProductStatus.SELLING).
                build().
                create()));
        Product product = productStockRepositoryImpl.getById(productEntity.getProductId());
        product.removeStock(100);

        // when
        int success = productStockRepositoryImpl.updateStockAndStatus(
                product.getProductId(),
                product.getStockQuantity(),
                product.getProductStatus(),
                product.getVersion());


        // then
        Product result = productStockRepositoryImpl.getById(productEntity.getProductId());
        assertThat(success).isEqualTo(1);
        assertThat(result.getProductStatus()).isEqualTo(ProductStatus.STOPPED);
        assertThat(result.getVersion()).isEqualTo(product.getVersion() + 1);
        assertThat(result.getStockQuantity()).isEqualTo(0);
    }

    @Test
    void 판매상태가_SELLING이_아닌_경우에는_업데이트가_발생하지_않음(){
        // given
        ProductEntity productEntity = productJpaRepository.save(ProductEntity.from(ProductFixture.
                builder().
                productStatus(ProductStatus.SELLING).
                build().
                create()));
        Product product = productStockRepositoryImpl.getById(productEntity.getProductId());
        product.removeStock(100);

        productStockRepositoryImpl.updateStockAndStatus(
                product.getProductId(),
                product.getStockQuantity(),
                product.getProductStatus(),
                product.getVersion());

        Product result = productStockRepositoryImpl.getById(productEntity.getProductId());

        // when
        int success = productStockRepositoryImpl.updateStockAndStatus(
                product.getProductId(),
                product.getStockQuantity(),
                product.getProductStatus(),
                product.getVersion());

        // then
        assertThat(success).isEqualTo(0);
    }

    @Test
    void 버전이_바뀐_경우_업데이트가_발생하지_않음(){
        // given
        ProductEntity productEntity = productJpaRepository.save(ProductEntity.from(ProductFixture.
                builder().
                productStatus(ProductStatus.SELLING).
                build().
                create()));

        Product product = productStockRepositoryImpl.getById(productEntity.getProductId());
        Integer pastVersion = product.getVersion();

        product.removeStock(100);
        productStockRepositoryImpl.updateStockAndStatus(
                product.getProductId(),
                product.getStockQuantity(),
                product.getProductStatus(),
                product.getVersion());

        Product updatedProduct = productStockRepositoryImpl.getById(productEntity.getProductId());

        // when
        int success = productStockRepositoryImpl.updateStockAndStatus(
                product.getProductId(),
                product.getStockQuantity(),
                product.getProductStatus(),
                pastVersion);

        // then
        assertThat(success).isEqualTo(0);
    }

}