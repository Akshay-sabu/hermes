package com.hodos.hermes.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
    private String field;
    private String message;
    private Object actual;
}
