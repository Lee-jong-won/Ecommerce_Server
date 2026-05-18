package jongwon.e_commerce.product.exception;

public class ProductPriceChangedException extends RuntimeException{

    public ProductPriceChangedException(int before, int after){
        super("이전 가격 : " + before + "이후 가격 :" + after);
    }


}
