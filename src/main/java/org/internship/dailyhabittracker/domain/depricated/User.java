package org.internship.dailyhabittracker.domain.depricated;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "USER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="ID",nullable = false, unique = true)
    private Long id;
    @Column(name ="USERNAME",nullable = false, length = 100)
    private String userName;
    @Column(name ="PASSWORD",nullable = false, length = 100)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE" ,nullable = false, length = 30)
    private Role role;
    @Column(name = "isActive")
    private boolean isActive;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Habit> habits;



}
