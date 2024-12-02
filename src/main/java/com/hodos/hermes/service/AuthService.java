package com.hodos.hermes.service;

import com.hodos.hermes.dto.dtos.UserDto;
import com.hodos.hermes.dto.responses.LoginResponse;

public interface AuthService {
    String sendOtp(String emailId);
    LoginResponse verifyOtpAndLogin(String emailId, String otp);
}
