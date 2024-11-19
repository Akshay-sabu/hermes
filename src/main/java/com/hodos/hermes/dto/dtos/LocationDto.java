package com.hodos.hermes.dto.dtos;
import lombok.Data;
import java.util.List;

@Data
public class LocationDto {
    private long id;
    private String name;
    private double longitude;
    private double latitude;
    private int noOfTags;
    private float overallRating;
    private List<BlogsDto> blogs;
}
