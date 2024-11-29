package com.hodos.hermes.dao.blog;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "sections")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(nullable = false)
    private String sectionTitle;

    @Column(columnDefinition = "TEXT")
    private String sectionContent;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Media> media;
}