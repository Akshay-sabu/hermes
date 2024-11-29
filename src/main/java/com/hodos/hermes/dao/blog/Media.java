package com.hodos.hermes.dao.blog;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String type;

    private String caption;
    private String altText;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;
}