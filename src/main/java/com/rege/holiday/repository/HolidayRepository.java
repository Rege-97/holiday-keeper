package com.rege.holiday.repository;

import com.rege.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Holiday h WHERE h.country.countryCode = :countryCode AND YEAR(h.date) = :year")
    void deleteByCountryAndYear(@Param("countryCode") String countryCode, @Param("year") int year);
}
