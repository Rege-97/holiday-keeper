package com.rege.holiday.repository;

import com.rege.holiday.entity.Country;
import com.rege.holiday.entity.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CountryRepositoryImpl implements CountryRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsert(List<Country> countries) {
        if (countries == null || countries.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO countries " +
                "(country_code, name) " +
                "VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, countries, 1000, (ps, country) -> {
            ps.setString(1, country.getCountryCode());
            ps.setString(2, country.getName());
        });
    }

}
