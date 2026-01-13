package jongwon.e_commerce.common.exception;

public class InfrastructureException extends RuntimeException{

    protected InfrastructureException(){}
    protected InfrastructureException(String message){
        super(message);
    }

}
