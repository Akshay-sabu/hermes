package com.hodos.hermes.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class CustomException extends RuntimeException{
    private ErrorTypes errorTypes;
    private List<Error> errors;
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
    public CustomException(ErrorTypes types,List<Error> errors){
        errorTypes = types;
        this.errors =errors;
    }
}
