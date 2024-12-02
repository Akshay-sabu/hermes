package com.hodos.hermes.dao.blog;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double latitude;

    private String address;
    private String city;
    private String country;

    private int noOfTags;
    private float overallRating;

    @ManyToMany(
            mappedBy = "locations",
            fetch = FetchType.LAZY
    )
    private Set<Blog> blogs = new HashSet<>();
}
