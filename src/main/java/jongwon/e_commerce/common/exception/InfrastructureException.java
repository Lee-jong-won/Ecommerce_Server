package jongwon.e_commerce.common.exception;

import lombok.Getter;

@Getter
public class InfrastructureException extends RuntimeException{
    public InfrastructureException(Throwable cause) {
    }

    public InfrastructureException(String message){
        super(message);
    }

}
