package com.hodos.hermes.dto.dtos;
import com.hodos.hermes.dao.user.Traveller;
import lombok.Data;

import java.util.List;
@Data
public class BlogsDto {
    private long blogId;
    private Traveller traveller;
    private String blogTitle;
    private String blogContent;
    private List<LocationDto> locations;
}
