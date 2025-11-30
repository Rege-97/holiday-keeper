package com.rege.holiday.mapper;

import com.rege.holiday.dto.HolidayDto;
import com.rege.holiday.entity.Country;
import com.rege.holiday.entity.Holiday;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class HolidayMapper {

    public Holiday toEntity(HolidayDto dto, Country country) {
        return Holiday.builder()
                .date(LocalDate.parse(dto.getDate()))
                .localName(dto.getLocalName())
                .name(dto.getName())
                .country(country)
                .fixed(dto.isFixed())
                .global(dto.isGlobal())
                .counties(dto.getCounties())
                .launchYear(dto.getLaunchYear())
                .types(dto.getTypes())
                .build();
    }
}
