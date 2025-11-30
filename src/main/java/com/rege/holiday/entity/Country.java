package com.rege.holiday.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "countries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country {

    @Id
    @Column(length = 2)
    private String countryCode;

    private String name;

    @Builder
    public Country(String countryCode, String name) {
        this.countryCode = countryCode;
        this.name = name;
    }
}
