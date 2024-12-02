package com.hodos.hermes.dto.blog;

import lombok.Data;

import java.util.List;

@Data
public class SectionDto {
    private Long sectionId;
    private Long blogId;
    private int orderIndex;
    private String sectionTitle;
    private String sectionContent;
    private List<MediaDto> mediaDtos;
}