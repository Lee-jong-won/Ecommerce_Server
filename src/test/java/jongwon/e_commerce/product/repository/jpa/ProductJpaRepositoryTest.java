package jongwon.e_commerce.product.repository.jpa;

import jongwon.e_commerce.product.infrastructure.jpa.ProductEntity;
import jongwon.e_commerce.product.infrastructure.jpa.ProductJpaRepository;
import jongwon.e_commerce.support.fixture.ProductFixture;
import jongwon.e_commerce.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = true)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Test
    void findBy_Id로_상품을_찾아올_수_있다(){
        // given
        Product product = ProductFixture.builder().build().create();
        ProductEntity productEntity = productJpaRepository.save(ProductEntity.from(product));

        // when
        ProductEntity result = productJpaRepository.findById(productEntity.getProductId()).orElseThrow();

        // then
        assertThat(result.getProductId()).isNotNull();
        assertThat(result.getProductName()).isEqualTo(productEntity.getProductName());
        assertThat(result.getProductPrice()).isEqualTo(productEntity.getProductPrice());
        assertThat(result.getProductStatus()).isEqualTo(productEntity.getProductStatus());
        assertThat(result.getStockQuantity()).isEqualTo(productEntity.getStockQuantity());
    }

}