package com.hodos.hermes.service;

import com.hodos.hermes.dto.dtos.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();

    String updateUser(UserDto userDto);
}
