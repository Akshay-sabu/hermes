package com.hodos.hermes.dao.blog;

import com.hodos.hermes.dao.user.Traveller;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traveller_id", nullable = false)
    private Traveller traveller;

    @Column(nullable = false)
    private String blogTitle;

    @Column(length = 500)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String blogContent;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Section> sections;

    @ManyToMany
    @JoinTable(
            name = "blog_location",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Location> locations;

    @ManyToMany
    @JoinTable(
            name = "blog_tags",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean isDraft = true;

    @Column(nullable = false)
    private boolean isPublished = false;

    private int viewCount = 0;
    private int likeCount = 0;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}