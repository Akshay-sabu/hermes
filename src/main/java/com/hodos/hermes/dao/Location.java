package com.hodos.hermes.dao;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "no_of_tags")
    private int noOfTags;

    @Column(name = "overall_rating")
    private float overallRating;

    @ManyToMany(mappedBy = "locations")
    private List<Blogs> blogs;
}
