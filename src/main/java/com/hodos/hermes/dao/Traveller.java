package com.hodos.hermes.dao;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="traveller")
public class Traveller {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "traveller_id")
    private String travellerId;

    @Column(name = "name")
    private String name;

    @Column(name = "email",unique = true)
    private String email;

    @OneToMany(mappedBy = "traveller")
    private List<Blogs> blogsList;

    @ManyToMany
    @JoinTable(
            name = "traveller_interests",
            joinColumns = @JoinColumn(name = "traveller_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private List<Interests> interests;
}
