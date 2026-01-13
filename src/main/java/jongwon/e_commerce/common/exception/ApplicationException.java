package jongwon.e_commerce.common.exception;

public class ApplicationException extends RuntimeException {
    protected ApplicationException(){}
    protected ApplicationException(String message){
        super(message);
    }
}
