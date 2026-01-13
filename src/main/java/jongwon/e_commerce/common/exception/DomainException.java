package jongwon.e_commerce.common.exception;

public abstract class DomainException extends RuntimeException {
    protected DomainException(){}
    protected DomainException(String message){
        super(message);
    }
}
