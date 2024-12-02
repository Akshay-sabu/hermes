package com.hodos.hermes.dto.blog;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class BlogDto {
    private Long blogId;
    private Long travellerId;
    private String blogTitle;
    private String summary;
    private String blogContent;
    private List<SectionDto> sectionDtos;
    private Set<LocationDto> locationDtos;
    private Set<TagDto> tagDtos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDraft = true;
    private boolean isPublished = false;
    private int viewCount = 0;
    private int likeCount = 0;
    private List<CommentDto> commentDtos;
}