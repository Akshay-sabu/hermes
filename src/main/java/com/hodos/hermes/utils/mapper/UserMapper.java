package com.hodos.hermes.utils.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodos.hermes.dao.user.User;
import com.hodos.hermes.dto.dtos.UserDto;

public class UserMapper {
    private UserMapper(){}
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static UserDto getUserDto(User user){
        return objectMapper.convertValue(user, UserDto.class);
    }
    public static User getUser(UserDto userDto){
        return objectMapper.convertValue(userDto, User.class);
    }
}
