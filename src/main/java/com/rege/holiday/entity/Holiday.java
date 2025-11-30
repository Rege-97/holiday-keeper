package com.rege.holiday.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "holidays")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(length = 200)
    private String localName;

    @Column(length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code")
    private Country country;

    private boolean fixed;

    private boolean global;

    @Column(length = 1000)
    private String counties;

    private Integer launchYear;

    @Column(length = 500)
    private String types;

    @Builder
    public Holiday(LocalDate date,
                   String localName,
                   String name,
                   Country country,
                   boolean fixed,
                   boolean global,
                   String counties,
                   Integer launchYear,
                   String types) {

        this.date = date;
        this.localName = localName;
        this.name = name;
        this.country = country;
        this.fixed = fixed;
        this.global = global;
        this.counties = counties;
        this.launchYear = launchYear;
        this.types = types;
    }
}
