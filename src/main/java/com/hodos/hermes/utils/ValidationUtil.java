package com.hodos.hermes.utils;


import com.hodos.hermes.exceptions.Error;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ValidationUtil {

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

    private static Error createValidationError(String field, String message, Object invalidValue) {
        return new Error(field,message,invalidValue);
    }
}
