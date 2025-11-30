package com.rege.holiday.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HolidayPageResponse {

    private List<HolidayResponse> holidays;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
