package com.hodos.hermes.dto.blog;

import lombok.Data;

import java.util.Set;

@Data
public class TagDto {
    private Long id;
    private String name;
    private Set<BlogDto> blogDtos;
}