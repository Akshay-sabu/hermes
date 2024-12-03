package com.hodos.hermes.service.impl;

import com.hodos.hermes.dao.user.User;
import com.hodos.hermes.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("${signkey}")
    private String SIGN_KEY;

    @Override
    public String generateToken(User user){
        return Jwts.builder()
                .setSubject(user.getEmail())
//                .setClaims(Map.of("roles",user.getRoles(),"user_id",user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+60000*5))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    @Override
    public String generateRefreshToken(HashMap<Object, Object> extraClaims, User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
//                .setClaims(Map.of("roles",user.getRoles(),"user_id",user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+604800000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUserName(String token){
        return extractClaims(token,Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userEmail = extractUserName(token);
        return (userDetails.equals(userDetails.getUsername()) || ! isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return extractClaims(token,Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaims(String token, Function<Claims,T> claimsResolvers){
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(SIGN_KEY);
        return Keys.hmacShaKeyFor(key);
    }
}
