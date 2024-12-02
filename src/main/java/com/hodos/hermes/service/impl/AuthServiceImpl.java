package com.hodos.hermes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodos.hermes.athena.OTPScrolls;
import com.hodos.hermes.athena.RoleScrolls;
import com.hodos.hermes.athena.UserScrolls;
import com.hodos.hermes.dao.user.OTPDao;
import com.hodos.hermes.dao.user.Role;
import com.hodos.hermes.dao.user.User;
import com.hodos.hermes.dto.responses.LoginResponse;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.ErrorTypes;
import com.hodos.hermes.service.AuthService;
import com.hodos.hermes.service.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.hodos.hermes.utils.GeneralUtils.isBlankStrings;
import static com.hodos.hermes.utils.Generate.GenerateOtp;
import static com.hodos.hermes.utils.ValidationUtil.validateEmail;
import static com.hodos.hermes.utils.mapper.UserMapper.getUserDto;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserScrolls userScrolls;
    private final OTPScrolls otpScrolls;
    private final EmailService emailService;
    private final JWTService jwtService;
    private final RoleScrolls roleScrolls;


    @Value("${otp.expiration.time}")
    private Integer OTP_EXPIRATION_TIME;

    @Autowired
    public AuthServiceImpl(UserScrolls userScrolls,
                           OTPScrolls otpScrolls,
                           EmailService emailService,
                           ObjectMapper objectMapper, JWTService jwtService, RoleScrolls roleScrolls) {
        this.userScrolls = userScrolls;
        this.otpScrolls = otpScrolls;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.roleScrolls = roleScrolls;
    }

    @Override
    public String sendOtp(String emailId) {
        try {
            validateEmail(emailId);
            Optional<OTPDao> existingOtp = otpScrolls.findByEmail(emailId);
            if (existingOtp.isPresent()) {
                OTPDao otpDao = existingOtp.get();
                if (!otpDao.isExpired()) {
                    throw new CustomException(ErrorTypes.ALREADY_EXISTING, "OTP is still valid");
                } else {
                    otpScrolls.delete(otpDao);
                }
            }

            OTPDao otpDao = createOtp(emailId);
            otpScrolls.save(otpDao);
            emailService.sendSimpleEmail(emailId, "Welcome", String.format("Your OTP is: %s", otpDao.getOtp()));
            return "OTP sent successfully";

        } catch (CustomException ce) {
            throw new CustomException(ce.getErrorTypes(), ce.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while sending OTP: ", e);
            throw new CustomException(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public LoginResponse verifyOtpAndLogin(String emailId, String otp) {
        try {
            validateEmail(emailId);
            if (isBlankStrings(otp)) {
                throw new CustomException(ErrorTypes.REQUIRED, "Otp Required");
            }

            Optional<OTPDao> otpDaoOptional = otpScrolls.findByEmail(emailId);
            if (otpDaoOptional.isEmpty()) {
                throw new CustomException(ErrorTypes.NOT_FOUND, "OTP not found");
            }

            OTPDao otpDao = otpDaoOptional.get();
            if (!otpDao.getOtp().equals(otp)) {
                throw new CustomException(ErrorTypes.INVALID_DATA, "Invalid OTP");
            }

            if (otpDao.getExpiryTime().isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorTypes.EXPIRED, "OTP has expired");
            }

            Optional<User> optionalTraveller = userScrolls.findByEmail(emailId);
            if (optionalTraveller.isEmpty()) {
                User user = new User();
                user.setEmail(emailId);
                user.setRoles(List.of(getRole()));
                User savedUser = userScrolls.save(user);
                emailService.sendSimpleEmail(savedUser.getEmail(),"Welcome","Welcome to the app Myre");

                return LoginResponse.builder()
                        .isOtpVerified(true)
                        .isTravellerExist(false)
                        //.userDto(getUserDto(user))
                        .build();
            }

            User user = optionalTraveller.get();
            otpScrolls.delete(otpDao);

            String token = jwtService.generateToken(user);

            String refreshToken = jwtService.generateRefreshToken(new HashMap<>(),user);

            return LoginResponse.builder()
                    .isOtpVerified(true)
                    .isTravellerExist(true)
                    .token(token)
                    .refreshToken(refreshToken)
                    //.userDto(getUserDto(user))
                    .build();

        } catch (CustomException ce) {
            throw new CustomException(ce.getErrorTypes(), ce.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while verifying OTP and logging in: ", e);
            throw new CustomException(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }
    private Role getRole(){
        String USER = "USER";
        Optional<Role> roleOptional = roleScrolls.findByName(USER);
        if(roleOptional.isPresent()){
            return roleOptional.get();
        }
        Role role = new Role();
        role.setName(USER);
        return roleScrolls.save(role);
    }

    private OTPDao createOtp(String emailId) {
        String otp = GenerateOtp();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME);
        return OTPDao.builder()
                .expiryTime(expireTime)
                .email(emailId)
                .otp(otp)
                .build();
    }
}
