package com.hodos.hermes.utils;


import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.Error;
import com.hodos.hermes.exceptions.ErrorTypes;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ValidationUtil {
    private static final List<String> VALID_EMAIL_DOMAINS = List.of("gmail.com");

    public static <T> List<Error> doObjectValidation(T obj) {
        List<Error> validationErrorList = new ArrayList<>();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        for (ConstraintViolation<T> violation : violations) {
            validationErrorList.add(createValidationError(violation.getPropertyPath().toString(),
                    violation.getMessage(),violation.getInvalidValue()));
        }
        return validationErrorList;
    }
    public static void validateEmail(String email){
        if(StringUtils.isBlank(email)){
            throw new CustomException(ErrorTypes.REQUIRED,"Email can not be null or empty");
        }
        String domain = email.split("@")[1];
        if(!VALID_EMAIL_DOMAINS.contains(domain)){
            throw new CustomException(ErrorTypes.INVALID_DATA,String.format("Email from %s  is not accepted",domain));
        }

    }

    private static Error createValidationError(String field, String message, Object invalidValue) {
        return new Error(field,message,invalidValue);
    }
}
