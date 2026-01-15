package jongwon.e_commerce.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "COMMON-002", "서버에서 처리할 수 없는 경우");

    private final int status;
    private final String code;
    private final String description;

    ErrorCode(int status, String code, String description){
        this.status = status;
        this.code = code;
        this.description = description;
    }
}
