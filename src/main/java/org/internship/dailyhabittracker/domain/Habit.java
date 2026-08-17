package org.internship.dailyhabittracker.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="ID",nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId",nullable = false)
    private User user;
}
