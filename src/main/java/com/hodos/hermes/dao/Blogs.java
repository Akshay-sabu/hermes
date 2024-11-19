package com.hodos.hermes.dao;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "blogs")
public class Blogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long blogId;

    @ManyToOne
    @JoinColumn(name = "traveller_id")
    private Traveller traveller;

    private String blogTitle;
    private String blogContent;

    @ManyToMany
    @JoinTable(
            name = "blog_location",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private List<Location> locations;
}
