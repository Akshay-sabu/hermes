package com.hodos.hermes.controller.eh;

import com.hodos.hermes.dto.responses.ErrorResponse;
import com.hodos.hermes.exceptions.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException customException){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .msg(customException.getErrorTypes().getMessage())
                .internalStatusCode(customException.getErrorTypes().getInternalStatusCode())
                .cause(customException.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse,customException.getErrorTypes().getHttpStatus());
    }
}
