package com.hodos.hermes.service;

import com.hodos.hermes.dao.user.User;
import com.hodos.hermes.dto.dtos.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();
    User getOrCreateUser(String emailId);
    String updateUser(UserDto userDto);
}
