package com.hodos.hermes.dto.blog;

import com.hodos.hermes.dao.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private BlogDto blogDto;
    private User user;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}