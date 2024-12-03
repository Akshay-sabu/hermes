package com.hodos.hermes.utils.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodos.hermes.dao.user.Interests;
import com.hodos.hermes.dao.user.User;
import com.hodos.hermes.dto.dtos.InterestsDto;
import com.hodos.hermes.dto.dtos.UserDto;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    private UserMapper(){}
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static UserDto getUserDto(User user){
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .interests(user.getInterests().stream()
                        .filter(Objects::nonNull)
                        .map(interests ->
                                InterestsDto.builder().id(interests.getId())
                                .interestName(interests.getInterestName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
    public static User getUpdatedUser(User user, UserDto userDto) {
        Optional.ofNullable(userDto.getName())
                .ifPresent(user::setName);

        Optional.ofNullable(userDto.getEmail())
                .ifPresent(user::setEmail);

        Optional.ofNullable(userDto.getUserId())
                .ifPresent(user::setUserId);

        Optional.ofNullable(userDto.getInterests())
                .ifPresent(interestDtos -> {
                    Set<Interests> interests = interestDtos.stream()
                            .map(interestDto -> Interests.builder()
                                    .id(interestDto.getId())
                                    .interestName(interestDto.getInterestName())
                                    .build())
                            .collect(Collectors.toSet());
                    user.setInterests(interests);
                });

        return user;
    }
}
