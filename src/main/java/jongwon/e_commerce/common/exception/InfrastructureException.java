package jongwon.e_commerce.common.exception;

import lombok.Getter;

@Getter
public class InfrastructureException extends RuntimeException{

    private final ErrorCode errorCode;

    public InfrastructureException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }
    public InfrastructureException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
    public InfrastructureException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

}
