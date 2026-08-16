package org.internship.dailyhabittracker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "HABIT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Habit {
}
