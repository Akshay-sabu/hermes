package com.hodos.hermes.controller.eh;

import com.hodos.hermes.dto.responses.ErrorResponse;
import com.hodos.hermes.exceptions.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
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
                .errorList(customException.getErrors())
                .build();
        return new ResponseEntity<>(errorResponse,customException.getErrorTypes().getHttpStatus());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException jwtException){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .msg(jwtException.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);

    }
}
