package com.hodos.hermes.dto.dtos;
import com.hodos.hermes.dao.user.Interests;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class UserDto {
    private long id;
    private String userId;

    @NotNull(message = "name required")
    private String name;

    @NotNull(message = "email required")
    @Email(message = "invalid email")
    private String email;

    private List<BlogsDto> blogsList;
    private List<Interests> interests;
}
