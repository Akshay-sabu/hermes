package com.hodos.hermes.dto.blog;

import lombok.Data;

import java.util.Set;

@Data
public class LocationDto {
    private Long locationId;
    private String name;
    private String description;
    private Double longitude;
    private Double latitude;

    private String address;
    private String city;
    private String country;

    private int noOfTags;
    private float overallRating;
    private Set<BlogDto> blogDtos;
}
