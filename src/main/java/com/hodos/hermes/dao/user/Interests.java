package com.hodos.hermes.dao.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Entity
@Table(name="interests")
public class Interests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "interest_name")
    private String interestName;

    @ManyToMany(mappedBy = "interests")
    private List<User> users;
}
