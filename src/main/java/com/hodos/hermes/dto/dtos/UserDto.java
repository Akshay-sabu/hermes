package com.hodos.hermes.dto.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private long id;

    private String userId;

    @NotNull(message = "name required")
    private String name;

    @NotNull(message = "email required")
    @Email(message = "invalid email")
    private String email;
    private Set<InterestsDto> interests;
}
