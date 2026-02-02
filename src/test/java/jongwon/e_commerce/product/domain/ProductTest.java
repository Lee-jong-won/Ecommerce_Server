package jongwon.e_commerce.product.domain;

import jongwon.e_commerce.product.exception.InvalidProductPriceException;
import jongwon.e_commerce.product.exception.InvalidProductStatusException;
import jongwon.e_commerce.product.exception.NotEnoughStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    public void 재고추가() {
        //Given
        Product product = Product.create("신발", 10000);

        //When
        product.addStock(10);

        //Then
        assertEquals(10, product.getStockQuantity());
    }

    @Test
    @DisplayName("판매대기 상태에서 재고가 있으면 판매를 시작한다")
    void startSelling_success() {
        // Given
        Product product = Product.create("신발", 10000);
        product.addStock(10);

        // When
        product.startSelling();

        // Then
        assertEquals(ProductStatus.SELLING, product.getProductStatus());
    }

    @Test
    @DisplayName("판매대기 상태 또는 판매중지 상태가 아니면 판매를 시작할 수 없다")
    void startSelling_fail_when_not_ready() {
        // Given
        Product product = Product.create("신발", 10000);
        product.addStock(10);
        product.setStatus(ProductStatus.ENDED);

        // When & Then
        assertThrows(InvalidProductStatusException.class, () -> product.startSelling());
    }

    @Test
    @DisplayName("재고가 없으면 판매를 시작할 수 없다")
    void startSelling_fail_when_no_stock() {
        // Given
        Product product = Product.create("신발", 10000);

        // When & Then
        assertThrows(NotEnoughStockException.class, () -> product.startSelling());
    }

    @Test
    @DisplayName("판매중 상태에서는 판매중지를 할 수 있다")
    void stopSelling_success() {
        // Given
        Product product = Product.create("신발", 10000);
        product.addStock(5);
        product.setStatus(ProductStatus.SELLING);

        // When
        product.stopSelling();

        // Then
        assertEquals(ProductStatus.STOPPED, product.getProductStatus());
    }

    @Test
    @DisplayName("판매중 상태가 아니면 판매중지를 할 수 없다")
    void stopSelling_fail_when_not_selling() {
        // Given
        Product product = Product.create("신발", 10000);
        product.setStatus(ProductStatus.READY);

        // When & Then
        assertThrows(InvalidProductStatusException.class, () -> product.stopSelling());
    }

    @Test
    @DisplayName("판매중지 상태에서 재고가 있으면 판매를 재개할 수 있다")
    void resumeSelling_success() {
        // Given
        Product product = Product.create("신발", 10000);
        product.addStock(5);
        product.setStatus(ProductStatus.STOPPED);

        // When
        product.resumeSelling();

        // Then
        assertEquals(ProductStatus.SELLING, product.getProductStatus());
    }

    @Test
    @DisplayName("판매중지 상태가 아니면 판매를 재개할 수 없다")
    void resumeSelling_fail_when_not_stopped() {
        // Given
        Product product = Product.create("신발", 10000);
        product.addStock(5);
        product.setStatus(ProductStatus.SELLING);

        // When & Then
        assertThrows(InvalidProductStatusException.class, () -> product.resumeSelling());
    }

    @Test
    @DisplayName("판매중지 상태라도 재고가 없으면 판매를 재개할 수 없다")
    void resumeSelling_fail_when_no_stock() {
        // Given
        Product product = Product.create("신발", 10000);
        product.setStatus(ProductStatus.STOPPED);
        // 재고 0

        // When & Then
        assertThrows(InvalidProductStatusException.class, () -> product.resumeSelling());
    }

    @Test
    @DisplayName("판매중지 상태에서는 판매종료를 할 수 있다")
    void endSelling_success() {
        // Given
        Product product = Product.create("신발", 10000);
        product.setStatus(ProductStatus.STOPPED);

        // When
        product.endSelling();

        // Then
        assertEquals(ProductStatus.ENDED, product.getProductStatus());
    }

    @Test
    @DisplayName("판매중지 상태가 아니면 판매종료를 할 수 없다")
    void endSelling_fail_when_not_stopped() {
        // Given
        Product product = Product.create("신발", 10000);
        product.setStatus(ProductStatus.SELLING);

        // When & Then
        assertThrows(InvalidProductStatusException.class, () -> product.endSelling());
    }

    @Test
    public void 재고감소_정상(){
        //Given
        Product product = Product.create("신발", 10000);
        product.addStock(10);
        product.setStatus(ProductStatus.SELLING);

        //When
        product.removeStock(5);

        //Then
        assertEquals(5, product.getStockQuantity());
        assertEquals(ProductStatus.SELLING, product.getProductStatus());
    }

    @Test
    @DisplayName("판매중이 아닌 상태에서 재고 차감 시 예외 발생")
    void removeStock_fail_when_not_selling() {
        // Given
        Product product = Product.create("신발", 10000);
        product.setStatus(ProductStatus.READY);

        // When and Then
        assertThrows(InvalidProductStatusException.class, () -> product.removeStock(1));
    }

    @Test
    @DisplayName("재고보다 많은 수량을 차감하면 예외 발생")
    void removeStock_fail_when_not_enough_stock() {
        // Given
        Product product = Product.create("신발", 10000);
        product.setStatus(ProductStatus.SELLING);
        product.addStock(3);   // 현재 재고 3

        // When & Then
        assertThrows(NotEnoughStockException.class, () -> product.removeStock(4));
    }

    @Test
    @DisplayName("재고가 0이 되면 자동으로 판매중지 상태가 된다")
    void removeStock_stop_selling_when_stock_zero() {
        // Given
        Product product = Product.create("신발", 10000);
        product.setStatus(ProductStatus.SELLING);
        product.addStock(2);

        // When
        product.removeStock(2);

        // Then
        assertEquals(0, product.getStockQuantity());
        assertEquals(ProductStatus.STOPPED, product.getProductStatus());
    }

    @Test
    @DisplayName("READY 상태에서는 재고를 변경할 수 있다")
    void changeStock_readyStatus_success() {
        // given
        Product product = Product.create("신발", 10000);
        product.changeStatus(ProductStatus.READY);

        // when
        product.changeStock(10);

        // then
        assertEquals(10, product.getStockQuantity());
    }

    @Test
    @DisplayName("STOPPED 상태에서는 재고를 변경할 수 있다")
    void changeStock_stoppedStatus_success() {
        // given
        Product product = Product.create("신발", 10000);
        product.changeStatus(ProductStatus.STOPPED);

        // when
        product.changeStock(20);

        // then
        assertEquals(20, product.getStockQuantity());
    }

    @Test
    @DisplayName("READY, STOPPED가 아닌 상태에서는 재고를 변경할 수 없다")
    void changeStock_invalidStatus_throwException() {
        // given
        Product product = Product.create("신발", 10000);
        product.changeStatus(ProductStatus.SELLING);

        // when & then
        assertThrows(
                InvalidProductStatusException.class,
                () -> product.changeStock(10)
        );
    }

    @Test
    @DisplayName("재고를 음수로 변경하려 하면 예외가 발생한다")
    void changeStock_negativeQuantity_throwException() {
        // given
        Product product = Product.create("신발", 10000);
        product.changeStatus(ProductStatus.READY);

        // when & then
        assertThrows(
                IllegalArgumentException.class,
                () -> product.changeStock(-1)
        );
    }

    @Test
    @DisplayName("READY 상태에서는 상품 기본 정보를 수정할 수 있다")
    void changeBasicInfo_readyStatus_success() {
        // given
        Product product = Product.create("신발", 10000);
        product.changeStatus(ProductStatus.READY);

        // when
        product.changeBasicInfo("운동화", 12000);

        // then
        assertEquals("운동화", product.getProductName());
        assertEquals(12000, product.getProductPrice());
    }

    @Test
    @DisplayName("STOPPED 상태에서는 상품 기본 정보를 수정할 수 있다")
    void changeBasicInfo_stoppedStatus_success() {
        // given
        Product product = Product.create("신발", 10000);
        product.changeStatus(ProductStatus.STOPPED);

        // when
        product.changeBasicInfo("구두", 15000);

        // then
        assertEquals("구두", product.getProductName());
        assertEquals(15000, product.getProductPrice());
    }

    @Test
    @DisplayName("READY, STOPPED가 아닌 상태에서는 상품 기본 정보를 수정할 수 없다")
    void changeBasicInfo_invalidStatus_throwException() {
        // given
        Product product = Product.create("신발", 10000);
        product.changeStatus(ProductStatus.SELLING);

        // when & then
        assertThrows(
                InvalidProductStatusException.class,
                () -> product.changeBasicInfo("운동화", 12000)
        );
    }

    @Test
    public void 가격_정보_수정(){
        //Given
        Product product = Product.create("신발", 10000);

        //When
        product.updateProductPrice(5000);

        //Then
        assertEquals(5000, product.getProductPrice());
    }

    @Test
    public void 가격_정보_수정_예외(){
        //Given
        Product product = Product.create("신발", 10000);

        //Then
        assertThrows(InvalidProductPriceException.class ,() -> product.updateProductPrice(0));
    }

}