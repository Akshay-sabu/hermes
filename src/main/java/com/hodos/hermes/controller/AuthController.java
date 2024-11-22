package com.hodos.hermes.controller;

import com.hodos.hermes.dto.dtos.TravellerDto;
import com.hodos.hermes.dto.responses.LoginResponse;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.Error;
import com.hodos.hermes.exceptions.ErrorTypes;
import com.hodos.hermes.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hodos.hermes.utils.ValidationUtil.doObjectValidation;

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
    private LoginResponse verifyOtpAndLogin(@RequestParam("email") @Email String email, @RequestParam("otp")String otp){
        return authService.verifyOtpAndLogin(email, otp);
    }

    @PostMapping("/reg")
    private String register(@RequestBody TravellerDto travellerDto){
        List<Error> errorList = doObjectValidation(travellerDto);
        if(errorList.isEmpty())
            return authService.registerTravellerIfNotExist(travellerDto);
        else throw new CustomException(ErrorTypes.INVALID_DATA,errorList);
    }
}
