package com.hodos.hermes.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewJWTResponse {
    private String token;
}
