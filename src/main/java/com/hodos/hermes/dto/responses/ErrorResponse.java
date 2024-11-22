package com.hodos.hermes.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hodos.hermes.exceptions.Error;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int internalStatusCode;
    private String cause;
    private String msg;
    private List<Error> errorList;
}
