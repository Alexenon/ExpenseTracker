package com.example.application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String type;

    /**
     * Figure out what kind of constraint I need
     * */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "timestamp_id")
    @Getter
    @Setter
    private Set<Timestamp> timestamps = new HashSet<>();

}
