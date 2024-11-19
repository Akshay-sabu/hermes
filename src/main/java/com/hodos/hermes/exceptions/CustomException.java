package com.hodos.hermes.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private ErrorTypes errorTypes;
    public CustomException(String msg){
        super(msg);
    }
    public CustomException(ErrorTypes types){
        errorTypes = types;
    }
    public CustomException(ErrorTypes types,String msg){
        super(msg);
        errorTypes = types;
    }
}
