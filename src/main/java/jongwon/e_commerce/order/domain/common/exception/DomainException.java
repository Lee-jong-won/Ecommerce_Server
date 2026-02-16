package jongwon.e_commerce.order.domain.common.exception;

public abstract class DomainException extends RuntimeException {
    protected DomainException(){}
    protected DomainException(String message){
        super(message);
    }
}
