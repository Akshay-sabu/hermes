package com.hodos.hermes.dto.responses;

import com.hodos.hermes.dto.dtos.TravellerDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private boolean isOtpVerified;
    private boolean isTravellerExist;
    private TravellerDto travellerDto;
}
