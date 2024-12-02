package com.hodos.hermes.dto.dtos;
import com.hodos.hermes.dao.user.User;
import lombok.Data;

import java.util.List;
@Data
public class BlogsDto {
    private long blogId;
    private User user;
    private String blogTitle;
    private String blogContent;
    private List<LocationDto> locations;
}
