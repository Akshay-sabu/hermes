package com.hodos.hermes.dao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="otp")
@Entity
public class OTPDao {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true)
    private String email;
    private String otp;
    private LocalDateTime expiryTime;
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

}
