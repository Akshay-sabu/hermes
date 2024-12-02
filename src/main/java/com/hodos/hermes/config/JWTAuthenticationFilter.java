package com.hodos.hermes.config;

import com.hodos.hermes.service.JWTService;
import com.hodos.hermes.service.UserService;
import com.hodos.hermes.exceptions.CustomException;
import com.hodos.hermes.exceptions.ErrorTypes;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");

            // Skip filter for auth endpoints
            if (request.getRequestURI().startsWith("/api/v1/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }

            if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Missing or invalid Authorization header");
                return;
            }

            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUserName(jwt);

            if (StringUtils.isEmpty(userEmail)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT token");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.userDetailsService()
                        .loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    // Create authentication token without credentials since we're using OTP
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No credentials needed for OTP-based auth
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(authToken);
                    SecurityContextHolder.setContext(securityContext);
                } else {
                    throw new CustomException(ErrorTypes.UN_AUTHORIZED,"Invalid or expired token");
                }
            }

            filterChain.doFilter(request, response);
        } catch (CustomException ce) {
            log.error("JWT Authentication error: {}", ce.getMessage());
            throw ce;
        } catch (Exception e) {
            log.error("Unexpected error in JWT Authentication filter", e);
            throw new CustomException(ErrorTypes.INTERNAL_SERVER_ERROR);
        }
    }
}