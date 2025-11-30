package com.rege.holiday.repository;

import com.rege.holiday.entity.Holiday;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepositoryCustom {

    void batchInsert(List<Holiday> holidays);

    List<Holiday> findByFilter(Integer year, String countryCode, LocalDate from, LocalDate to, String type,
                               Long lastId, int size);
}
