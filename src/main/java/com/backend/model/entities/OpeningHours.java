package com.backend.model.entities;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "opening_hours")
public class OpeningHours {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime openTime;
    private LocalTime closeTime;

    @PrePersist
    @PreUpdate
    private void validateTimeRange () {
        if (openTime != null && closeTime != null && !closeTime.isAfter(openTime)) {
            throw new IllegalArgumentException("End time must be later then start time");
        }
    }
}
