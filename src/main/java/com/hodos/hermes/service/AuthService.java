package com.hodos.hermes.service;

import com.hodos.hermes.dto.requests.NewJWTRequest;
import com.hodos.hermes.dto.responses.LoginResponse;
import com.hodos.hermes.dto.responses.NewJWTResponse;

public interface AuthService {
    String sendAuthOtp(String emailId);
    LoginResponse verifyOtpAndLogin(String emailId, String otp);
    NewJWTResponse getNewJwtToken(NewJWTRequest jwtRequest);
}
