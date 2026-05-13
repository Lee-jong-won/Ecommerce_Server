package jongwon.e_commerce.product.exception;

public class ProductNameChangedException extends RuntimeException{
    public ProductNameChangedException(String message){
        super(message);
    }
}
