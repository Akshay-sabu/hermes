package com.hodos.hermes.exceptions;

import lombok.Data;

@Data
public class Error {
    private String field;
    private String message;
}
