package com.hodos.hermes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodos.hermes.athena.OTPScrolls;
import com.hodos.hermes.athena.TravellerScrolls;
import com.hodos.hermes.dao.OTPDao;
import com.hodos.hermes.dao.Traveller;
import com.hodos.hermes.dto.responses.LoginResponse;
import com.hodos.hermes.dto.dtos.TravellerDto;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.ErrorTypes;
import com.hodos.hermes.service.AuthService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.hodos.hermes.utils.GeneralUtils.IsBlankStrings;
import static com.hodos.hermes.utils.Generate.GenerateOtp;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final TravellerScrolls travellerScrolls;
    private final OTPScrolls otpScrolls;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Value("${otp.expiration.time}")
    private Integer OTP_EXPIRATION_TIME;

    @Autowired
    public AuthServiceImpl(TravellerScrolls travellerScrolls,
                           OTPScrolls otpScrolls,
                           EmailService emailService,
                           ObjectMapper objectMapper) {
        this.travellerScrolls = travellerScrolls;
        this.otpScrolls = otpScrolls;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String sendOtp(String emailId) {
        try {
            if (StringUtils.isBlank(emailId)) {
                throw new CustomException(ErrorTypes.REQUIRED, "Email address required");
            }

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
            throw new CustomException(ErrorTypes.ERROR, "Something went wrong");
        }
    }

    @Override
    public LoginResponse verifyOtpAndLogin(String emailId, String otp) {
        try {
            if (IsBlankStrings(emailId, otp)) {
                throw new CustomException(ErrorTypes.REQUIRED, "Invalid request");
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

            Optional<Traveller> optionalTraveller = travellerScrolls.findByEmail(emailId);
            if (optionalTraveller.isEmpty()) {
                return LoginResponse.builder()
                        .isOtpVerified(true)
                        .isTravellerExist(false)
                        .travellerDto(null)
                        .build();
            }

            Traveller traveller = optionalTraveller.get();
            TravellerDto travellerDto = objectMapper.convertValue(traveller, TravellerDto.class);

            otpScrolls.delete(otpDao);

            return LoginResponse.builder()
                    .isOtpVerified(true)
                    .isTravellerExist(true)
                    .travellerDto(travellerDto)
                    .build();

        } catch (CustomException ce) {
            throw new CustomException(ce.getErrorTypes(), ce.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while verifying OTP and logging in: ", e);
            throw new CustomException(ErrorTypes.ERROR, "Something went wrong");
        }
    }

    @Override
    public String registerTravellerIfNotExist(TravellerDto travellerDto) {
        try {
            String email = travellerDto.getEmail();
            Optional<Traveller> optionalTraveller = travellerScrolls.findByEmail(email);
            if (optionalTraveller.isPresent()) {
                throw new CustomException(ErrorTypes.ALREADY_EXISTING, "User exist");
            }
            Traveller traveller = objectMapper.convertValue(travellerDto, Traveller.class);
            travellerScrolls.save(traveller);
            return "Saved successfully";
        } catch (CustomException ce) {
            throw new CustomException(ce.getErrorTypes(), ce.getMessage());
        } catch (Exception e) {
            log.error("Error - > ", e);
            throw new CustomException(ErrorTypes.ERROR);
        }

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
