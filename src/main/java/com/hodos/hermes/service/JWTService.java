package com.hodos.hermes.service;

import com.hodos.hermes.dao.user.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;

public interface JWTService {
    String generateToken(User user);

    String extractUserName(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(HashMap<Object, Object> objectObjectHashMap, User user);
}
