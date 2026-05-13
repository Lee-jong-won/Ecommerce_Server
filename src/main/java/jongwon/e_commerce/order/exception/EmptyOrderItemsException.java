package jongwon.e_commerce.order.exception;

public class EmptyOrderItemsException extends RuntimeException{

    public EmptyOrderItemsException(String message){
        super(message);
    }

}
