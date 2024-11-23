package com.hodos.hermes.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorTypes {
    ALREADY_EXISTING("Already existing", 1000, HttpStatus.CONFLICT),
    INVALID_DATA("Invalid data", 1001, HttpStatus.BAD_REQUEST),
    EXPIRED("Expired", 1002, HttpStatus.REQUEST_TIMEOUT),
    REQUIRED("field required",1003,HttpStatus.BAD_REQUEST),
    ERROR("Try again later",1004,HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("Not found",1005,HttpStatus.NOT_FOUND);


    private final String message;
    private final HttpStatus httpStatus;
    private final int internalStatusCode;

    ErrorTypes(String message, int internalStatusCode, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.internalStatusCode = internalStatusCode;
    }
}
