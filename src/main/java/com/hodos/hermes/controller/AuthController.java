package com.hodos.hermes.controller;

import com.hodos.hermes.dto.requests.NewJWTRequest;
import com.hodos.hermes.dto.responses.LoginResponse;
import com.hodos.hermes.dto.responses.NewJWTResponse;
import com.hodos.hermes.service.AuthService;
import jakarta.validation.constraints.Email;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email")@Email String email){
        return authService.sendAuthOtp(email);
    }

    @PostMapping("/verify-otp")
    public LoginResponse verifyOtpAndLogin(@RequestParam("email") @Email String email, @RequestParam("otp")String otp){
        return authService.verifyOtpAndLogin(email, otp);
    }
    @PostMapping("/request-token")
    public NewJWTResponse getNewTokenViaRefreshToken(@RequestBody NewJWTRequest jwtRequest){
        return authService.getNewJwtToken(jwtRequest);
    }

}
