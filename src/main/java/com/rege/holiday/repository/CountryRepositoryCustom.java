package com.rege.holiday.repository;

import com.rege.holiday.entity.Country;

import java.util.List;

public interface CountryRepositoryCustom {
    
    void batchInsert(List<Country> countries);
}
