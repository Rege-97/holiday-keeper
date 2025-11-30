package com.rege.holiday.mapper;

import com.rege.holiday.dto.CountryDto;
import com.rege.holiday.entity.Country;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper {

    public Country toEntity(CountryDto dto) {
        return Country.builder()
                .countryCode(dto.getCountryCode())
                .name(dto.getName())
                .build();
    }
}
