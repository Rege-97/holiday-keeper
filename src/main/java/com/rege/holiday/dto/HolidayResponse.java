package com.rege.holiday.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class HolidayResponse {

    private Long id;
    private LocalDate date;
    private String localName;
    private String name;
    private String countryName;
    private boolean fixed;
    private boolean global;
    private List<String> counties;
    private Integer launchYear;
    private List<String> types;
}
