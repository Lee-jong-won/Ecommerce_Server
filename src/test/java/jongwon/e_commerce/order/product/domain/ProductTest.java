package jongwon.e_commerce.order.product.domain;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.exception.InvalidProductPriceException;
import jongwon.e_commerce.product.exception.InvalidProductStatusException;
import jongwon.e_commerce.product.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    /*
     * 상품 생성 테스트
     * */
    @Test
    void Product를_생성하면_READY_상태와_가격이_설정된다() {
        // when
        Product product = Product.from("노트북", 100000);

        // then
        assertThat(product.getProductName()).isEqualTo("노트북");
        assertThat(product.getProductPrice()).isEqualTo(100000);
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.READY);
    }

    /*
     * 재고 증가 및 감소 테스트
     * */
    @Test
    void 판매중이_아니면_재고_추가시_예외가_발생한다() {
        // given
        Product product = Product.from("노트북", 100000);

        // when && then
        assertThatThrownBy(() -> product.addStock(10))
                .isInstanceOf(InvalidProductStatusException.class);
    }

    @Test
    void 판매중_상태에서는_재고가_증가한다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);

        // when
        product.addStock(10);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }

    @Test
    void 재고보다_많이_차감하면_예외가_발생한다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);
        product.addStock(5);

        // when && then
        assertThatThrownBy(() -> product.removeStock(10))
                .isInstanceOf(NotEnoughStockException.class);
    }

    @Test
    void 재고를_차감하면_정상적으로_감소한다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);
        product.addStock(10);

        // when
        product.removeStock(3);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(7);
    }

    @Test
    void 재고가_0이되면_STOPPED_상태로_변경된다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);
        product.addStock(5);

        // when
        product.removeStock(5);

        // then
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.STOPPED);
    }

    /*
    * 상품 상태 변경 메소드 테스트
    * */
    void READY_상태에서_판매를_시작할_수_있다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.addStock(10);

        // when
        product.startSelling();

        // then
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.SELLING);
    }

    @Test
    void 재고가_없으면_판매를_시작할_수_없다() {
        // given
        Product product = Product.from("노트북", 100000);

        // when && then
        assertThatThrownBy(() -> product.startSelling())
                .isInstanceOf(NotEnoughStockException.class);
    }

    @Test
    void SELLING_상태가_아니면_판매중지를_할_수_없다() {
        // given
        Product product = Product.from("노트북", 100000);

        // when && then
        assertThatThrownBy(() -> product.stopSelling())
                .isInstanceOf(InvalidProductStatusException.class);
    }

    @Test
    void STOPPED_상태에서_판매를_재개할_수_있다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.STOPPED);
        product.changeStock(10);

        // when
        product.resumeSelling();

        // then
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.SELLING);
    }

    @Test
    void STOPPED_상태에서_판매종료가_가능하다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.STOPPED);

        // when
        product.endSelling();

        // then
        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.ENDED);
    }

    /*
     * 가격 변경 메소드 테스트
     * */
    @Test
    void READY_상태에서_가격을_정상적으로_변경할_수_있다() {
        // given
        Product product = Product.from("노트북", 100000);

        // when
        product.updateProductPrice(200000);

        // then
        assertThat(product.getProductPrice()).isEqualTo(200000);
    }

    @Test
    void STOPPED_상태에서도_가격을_변경할_수_있다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.STOPPED);

        // when
        product.updateProductPrice(150000);

        // then
        assertThat(product.getProductPrice()).isEqualTo(150000);
    }

    @Test
    void SELLING_상태에서는_가격_변경시_예외가_발생한다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);

        // when & then
        assertThatThrownBy(() -> product.updateProductPrice(200000))
                .isInstanceOf(InvalidProductStatusException.class)
                .hasMessage("상품 가격 수정 불가 상태");
    }

    @Test
    void 가격이_0_이하면_예외가_발생한다() {
        // given
        Product product = Product.from("노트북", 100000);

        // when & then
        assertThatThrownBy(() -> product.updateProductPrice(0))
                .isInstanceOf(InvalidProductPriceException.class)
                .hasMessage("price cannot be negative or zero");
    }

    @Test
    void 음수_가격이면_예외가_발생한다() {
        // given
        Product product = Product.from("노트북", 100000);

        // when & then
        assertThatThrownBy(() -> product.updateProductPrice(-100))
                .isInstanceOf(InvalidProductPriceException.class);
    }


}