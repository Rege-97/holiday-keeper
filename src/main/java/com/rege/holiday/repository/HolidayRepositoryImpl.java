package com.rege.holiday.repository;

import com.rege.holiday.entity.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<Holiday> holidays) {
        if (holidays == null || holidays.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO holidays " +
                "(date, local_name, name, country_code, fixed, global, counties, launch_year, types) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, holidays, 1000, (ps, holiday) -> {
            ps.setDate(1, Date.valueOf(holiday.getDate()));
            ps.setString(2, holiday.getLocalName());
            ps.setString(3, holiday.getName());
            ps.setString(4, holiday.getCountry().getCountryCode());
            ps.setBoolean(5, holiday.isFixed());
            ps.setBoolean(6, holiday.isGlobal());
            ps.setString(7, String.join(",", holiday.getCounties() != null ? holiday.getCounties() : List.of()));
            ps.setObject(8, holiday.getLaunchYear());
            ps.setString(9, String.join(",", holiday.getTypes() != null ? holiday.getTypes() : List.of()));
        });
    }
}
