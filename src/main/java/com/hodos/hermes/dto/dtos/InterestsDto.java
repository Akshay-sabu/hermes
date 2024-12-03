package com.hodos.hermes.dto.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class InterestsDto {
    private long id;
    private String interestName;
    private List<UserDto> travellers;
}
