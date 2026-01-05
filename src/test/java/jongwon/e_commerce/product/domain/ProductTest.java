package jongwon.e_commerce.product.domain;

import jongwon.e_commerce.product.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    public void 재고추가() {
        //Given
        Product product = new Product("신발", 10000, 10);

        //When
        product.addStock(10);

        //Then
        assertEquals(20, product.getStockQuantity());
    }

    @Test
    public void 재고감소_정상(){
        //Given
        Product product = new Product("신발", 10000, 10);

        //When
        product.removeStock(5);

        //Then
        assertEquals(5, product.getStockQuantity());
    }

    @Test
    public void 재고감소_예외(){
        //Given
        Product product = new Product("신발", 10000, 10);

        //Then
        assertThrows(NotEnoughStockException.class ,() -> product.removeStock(20));
    }

}