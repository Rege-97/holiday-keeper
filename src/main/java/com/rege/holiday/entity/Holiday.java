package com.rege.holiday.entity;

import com.rege.holiday.common.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Table(
        name = "holidays",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holiday_unique",
                        columnNames = {
                                "date",
                                "country_code",
                                "name",
                                "local_name",
                                "counties",
                                "types"
                        }
                )
        }
)
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

    @Convert(converter = StringListConverter.class)
    private List<String> counties;

    private Integer launchYear;

    @Convert(converter = StringListConverter.class)
    private List<String> types;

    @Builder
    public Holiday(LocalDate date,
                   String localName,
                   String name,
                   Country country,
                   boolean fixed,
                   boolean global,
                   List<String> counties,
                   Integer launchYear,
                   List<String> types) {

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
