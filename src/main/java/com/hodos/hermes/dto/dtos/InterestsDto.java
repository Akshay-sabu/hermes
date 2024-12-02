package com.hodos.hermes.dto.dtos;

import lombok.Data;

import java.util.List;
@Data
public class InterestsDto {
    private long id;
    private String interestName;
    private List<UserDto> travellers;
}
