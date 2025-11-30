package com.rege.holiday.repository;

import com.rege.holiday.entity.Holiday;

import java.util.List;

public interface HolidayRepositoryCustom {

    void batchInsert(List<Holiday> holidays);
}
