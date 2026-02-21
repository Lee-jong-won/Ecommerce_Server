package jongwon.e_commerce.order.domain.common.exception;

import lombok.Getter;

@Getter
public class InfrastructureException extends RuntimeException{
    public InfrastructureException(Throwable cause) {
    }

    public InfrastructureException(String message){
        super(message);
    }

}
