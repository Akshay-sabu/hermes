package com.hodos.hermes.dao;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Table(name="otp")
@Entity
public class OTPDao {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long travellerId;
    private String otp;
    private LocalDateTime expiryTime;

    public OTPDao(long travellerId, String otp, LocalDateTime expiryTime) {
        this.travellerId = travellerId;
        this.otp = otp;
        this.expiryTime = expiryTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

}
