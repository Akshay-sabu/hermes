package com.hodos.hermes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodos.hermes.dto.responses.ErrorResponse;
import com.hodos.hermes.service.JWTService;
import com.hodos.hermes.service.UserService;
import com.hodos.hermes.exceptions.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
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
                handleError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
                return;
            }

            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUserName(jwt);

            if (StringUtils.isEmpty(userEmail)) {
                handleError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userService.userDetailsService()
                            .loadUserByUsername(userEmail);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        securityContext.setAuthentication(authToken);
                        SecurityContextHolder.setContext(securityContext);

                        filterChain.doFilter(request, response);
                    } else {
                        handleError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    }
                } catch (CustomException ce) {
                    handleCustomError(response, ce);
                }
            }
        } catch (ExpiredJwtException ee){
            log.error("Token expired");
            handleError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
        catch (Exception e) {
            log.error("Unexpected error in JWT Authentication filter", e);
            handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void handleError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .msg(message)
                .internalStatusCode(status)
                .cause(message)
                .build();
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

    private void handleCustomError(HttpServletResponse response, CustomException ce) throws IOException {
        response.setStatus(ce.getErrorTypes().getHttpStatus().value());
        response.setContentType("application/json");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .msg(ce.getErrorTypes().getMessage())
                .internalStatusCode(ce.getErrorTypes().getInternalStatusCode())
                .cause(ce.getMessage())
                .errorList(ce.getErrors())
                .build();
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}