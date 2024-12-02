package com.hodos.hermes.dto.responses;

import com.hodos.hermes.dto.dtos.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private boolean isOtpVerified;
    private boolean isTravellerExist;
    private String token;
    private String refreshToken;
    private UserDto userDto;
}
