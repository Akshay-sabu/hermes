package com.hodos.hermes.dto.requests;

import lombok.Data;

@Data
public class NewJWTRequest {
    private String refreshToken;
}
