package com.hodos.hermes.controller;

import com.hodos.hermes.dto.dtos.TravellerDto;
import com.hodos.hermes.dto.responses.LoginResponse;
import com.hodos.hermes.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    private String sendOtp(@RequestParam("email") String email){
        return authService.sendOtp(email);
    }

    @PostMapping("/verify-otp")
    private LoginResponse verifyOtpAndLogin(@RequestParam("email")String email, @RequestParam("otp")String otp){
        return authService.verifyOtpAndLogin(email, otp);
    }

    @PostMapping("/reg")
    private String register(@RequestBody @Valid TravellerDto travellerDto){
        return authService.registerTravellerIfNotExist(travellerDto);
    }
}
