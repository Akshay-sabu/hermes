package com.hodos.hermes.dto.blog;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MediaDto {
    private Long id;
    private Long sectionId;
    private String url;
    private String type;
    private String caption;
    private String altText;
    private LocalDateTime uploadedAt;
}