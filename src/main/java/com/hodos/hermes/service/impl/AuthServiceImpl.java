package com.hodos.hermes.service.impl;

import com.hodos.hermes.athena.OTPScrolls;
import com.hodos.hermes.athena.RoleScrolls;
import com.hodos.hermes.athena.UserScrolls;
import com.hodos.hermes.dao.user.OTPDao;
import com.hodos.hermes.dao.user.Role;
import com.hodos.hermes.dao.user.User;
import com.hodos.hermes.dto.requests.NewJWTRequest;
import com.hodos.hermes.dto.responses.LoginResponse;
import com.hodos.hermes.dto.responses.NewJWTResponse;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.ErrorTypes;
import com.hodos.hermes.service.AuthService;
import com.hodos.hermes.service.JWTService;
import com.hodos.hermes.service.UserService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserService userService;


    @Value("${otp.expiration.time}")
    private Integer OTP_EXPIRATION_TIME;

    @Autowired
    public AuthServiceImpl(UserScrolls userScrolls,
                           OTPScrolls otpScrolls,
                           EmailService emailService, JWTService jwtService, RoleScrolls roleScrolls, UserService userService) {
        this.userScrolls = userScrolls;
        this.otpScrolls = otpScrolls;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.roleScrolls = roleScrolls;
        this.userService = userService;
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
        validateInputs(emailId, otp);

        OTPDao otpDao = validateAndGetOtp(emailId, otp);
        User user = getOrCreateUser(emailId);

        otpScrolls.delete(otpDao);

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        return buildLoginResponse(user, token, refreshToken);
    }

    private void validateInputs(String emailId, String otp) {
        validateEmail(emailId);
        if (isBlankStrings(otp)) {
            throw new CustomException(ErrorTypes.REQUIRED, "OTP Required");
        }
    }

    private OTPDao validateAndGetOtp(String emailId, String otp) {
        OTPDao otpDao = otpScrolls.findByEmail(emailId)
                .orElseThrow(() -> new CustomException(ErrorTypes.NOT_FOUND, "OTP not found"));

        if (!otpDao.getOtp().equals(otp)) {
            throw new CustomException(ErrorTypes.INVALID_DATA, "Invalid OTP");
        }

        if (otpDao.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorTypes.EXPIRED, "OTP has expired");
        }

        return otpDao;
    }

    private User getOrCreateUser(String emailId) {
        return userScrolls.findByEmail(emailId)
                .orElseGet(() -> createNewUser(emailId));
    }

    private User createNewUser(String emailId) {
        User user = new User();
        user.setEmail(emailId);
        user.setRoles(List.of(getRole()));

        User savedUser = userScrolls.save(user);
        sendWelcomeEmail(savedUser);

        return savedUser;
    }

    private void sendWelcomeEmail(User user) {
        try {
            emailService.sendSimpleEmail(user.getEmail(), "Welcome", "Welcome to the app Myre");
        } catch (Exception e) {
            log.warn("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private LoginResponse buildLoginResponse(User user, String token, String refreshToken) {
        return LoginResponse.builder()
                .isOtpVerified(true)
                .isTravellerExist(userScrolls.existsByEmail(user.getEmail()))
                .token(token)
                .refreshToken(refreshToken)
                .userDto(getUserDto(user))
                .build();
    }

    @Override
    public NewJWTResponse getNewJwtToken(NewJWTRequest jwtRequest) {
        try {
            String refreshToken = jwtRequest.getRefreshToken();
            if (StringUtils.isBlank(refreshToken) || !refreshToken.startsWith("Bearer ")) {
                throw new CustomException(ErrorTypes.REQUIRED, "Invalid or empty refresh token");
            }
            String rt = refreshToken.substring(7);
            String userEmail = jwtService.extractUserName(rt);
            User user = userScrolls.findByEmail(userEmail).orElseThrow(() ->
                    new CustomException(ErrorTypes.NOT_FOUND, "User not found"));

            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
            if (!jwtService.isTokenValid(rt, userDetails)) {
                throw new CustomException(ErrorTypes.INVALID_DATA, "Invalid refresh token");
            }
            return NewJWTResponse.builder()
                    .token(jwtService.generateToken(user))
                    .build();
        } catch (CustomException ce) {
            throw ce;
        } catch (Exception e) {
//            log.error("Error occurred while refreshing JWT for user: {}. Message: {}", jwtRequest.getUserEmail(), e.getMessage(), e);
            throw new CustomException(ErrorTypes.INTERNAL_SERVER_ERROR, "An error occurred while generating the JWT token");
        }
    }


    private Role getRole() {
        String USER = "USER";
        Optional<Role> roleOptional = roleScrolls.findByName(USER);
        if (roleOptional.isPresent()) {
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
