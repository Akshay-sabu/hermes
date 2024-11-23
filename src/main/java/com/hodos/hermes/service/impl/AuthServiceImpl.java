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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.hodos.hermes.utils.GeneralUtils.isBlankStrings;
import static com.hodos.hermes.utils.Generate.GenerateOtp;
import static com.hodos.hermes.utils.ValidationUtil.validateEmail;

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
            throw new CustomException(ErrorTypes.ERROR, "Something went wrong");
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

            Optional<Traveller> optionalTraveller = travellerScrolls.findByEmail(emailId);
            if (optionalTraveller.isEmpty()) {
                Traveller traveller = new Traveller();
                traveller.setEmail(emailId);
                Traveller savedTraveller = travellerScrolls.save(traveller);
                emailService.sendSimpleEmail(savedTraveller.getEmail(),"Welcome","Welcome to the app Myre");

                return LoginResponse.builder()
                        .isOtpVerified(true)
                        .isTravellerExist(false)
                        .travellerDto(getTravellerDto(traveller))
                        .build();
            }

            Traveller traveller = optionalTraveller.get();
            otpScrolls.delete(otpDao);

            return LoginResponse.builder()
                    .isOtpVerified(true)
                    .isTravellerExist(true)
                    .travellerDto(getTravellerDto(traveller))
                    .build();

        } catch (CustomException ce) {
            throw new CustomException(ce.getErrorTypes(), ce.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while verifying OTP and logging in: ", e);
            throw new CustomException(ErrorTypes.ERROR, "Something went wrong");
        }
    }

    @Override
    public String updateTraveller(TravellerDto travellerDto) {
        try {
            String email = travellerDto.getEmail();
            Optional<Traveller> optionalTraveller = travellerScrolls.findByEmail(email);
            if (optionalTraveller.isEmpty()) {
                throw new CustomException(ErrorTypes.NOT_FOUND,String.format("Account Not fount -> %s",email));
            }
            long pk = optionalTraveller.get().getId();
            Traveller traveller = getTraveller(travellerDto);
            traveller.setId(pk);
            travellerScrolls.save(traveller);
            return "Saved successfully";
        } catch (CustomException ce) {
            throw new CustomException(ce.getErrorTypes(), ce.getMessage());
        } catch (Exception e) {
            log.error("Error - > ", e);
            throw new CustomException(ErrorTypes.ERROR);
        }

    }

    // ----private helper methods-----
    private TravellerDto getTravellerDto(Traveller traveller){
        return objectMapper.convertValue(traveller, TravellerDto.class);
    }
    private Traveller getTraveller(TravellerDto travellerDto){
        return objectMapper.convertValue(travellerDto, Traveller.class);
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
