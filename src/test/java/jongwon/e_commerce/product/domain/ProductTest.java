package jongwon.e_commerce.product.domain;

import jongwon.e_commerce.product.exception.InvalidProductPriceException;
import jongwon.e_commerce.product.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    public void 재고추가() {
        //Given
        Product product = Product.create("신발", 10000, 10);

        //When
        product.addStock(10);

        //Then
        assertEquals(20, product.getStockQuantity());
    }

    @Test
    public void 재고감소_정상(){
        //Given
        Product product = Product.create("신발", 10000, 10);

        //When
        product.removeStock(5);

        //Then
        assertEquals(5, product.getStockQuantity());
    }

    @Test
    public void 재고감소_예외(){
        //Given
        Product product = Product.create("신발", 10000, 10);

        //Then
        assertThrows(NotEnoughStockException.class ,() -> product.removeStock(20));
    }

    @Test
    public void 가격_정보_수정(){
        //Given
        Product product = Product.create("신발", 10000, 10);

        //When
        product.updateProductPrice(5000);

        //Then
        assertEquals(5000, product.getProductPrice());
    }

    @Test
    public void 가격_정보_수정_예외(){
        //Given
        Product product = Product.create("신발", 10000, 10);

        //Then
        assertThrows(InvalidProductPriceException.class ,() -> product.updateProductPrice(0));
    }

}