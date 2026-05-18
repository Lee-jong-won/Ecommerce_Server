package jongwon.e_commerce.product.exception;

public class ProductNameChangedException extends RuntimeException{
    public ProductNameChangedException(String before, String after){
        super("이전 이름 : " + before + "이후 이름 :" + after);
    }
}
