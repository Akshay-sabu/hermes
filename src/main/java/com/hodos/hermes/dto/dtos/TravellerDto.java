package com.hodos.hermes.dto.dtos;
import com.hodos.hermes.dao.Interests;
import lombok.Data;

import java.util.List;
@Data
public class TravellerDto {
    private long id;
    private String travellerId;
    private String name;
    private String email;
    private List<BlogsDto> blogsList;
    private List<Interests> interests;
}
