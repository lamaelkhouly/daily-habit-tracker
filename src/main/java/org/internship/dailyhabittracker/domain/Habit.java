package org.internship.dailyhabittracker.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HABIT")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="ID",nullable = false, unique = true)
    private Long id;
    @Column(name ="NAME",nullable = false, length = 100)
    private String name;
    @Column(name = "DESCRIPTION", length = 500)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY" ,nullable = false, length = 30)
    private Category category;
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS" ,nullable = false, length = 30)
    private HabitStatus habitStatus;
    @Column(name = "FREQUENCY",nullable = false,length=30)
    private String frequency;

    @ManyToOne
    @JoinColumn(name = "userId",nullable = false)
    private User user;
}
