package com.rege.holiday.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rege.holiday.entity.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static com.rege.holiday.entity.QHoliday.holiday;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

    private final JPAQueryFactory queryFactory;
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

    @Override
    public List<Holiday> findByFilter(Integer year, String countryCode, LocalDate from, LocalDate to, String type,
                                      Long lastId, int size) {
        return queryFactory.selectFrom(holiday)
                .where(
                        yearEq(year),
                        countryCodeEq(countryCode),
                        typeContains(type),
                        dateRange(from, to),
                        idLt(lastId)
                )
                .orderBy(holiday.id.desc())
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression yearEq(Integer year) {
        return year != null ? holiday.date.year().eq(year) : null;
    }

    private BooleanExpression countryCodeEq(String countryCode) {
        return countryCode != null ? holiday.country.countryCode.eq(countryCode) : null;
    }

    private BooleanExpression typeContains(String type) {
        return StringUtils.hasText(type) ? holiday.types.contains(type) : null;
    }

    private BooleanExpression dateRange(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;
        if (from == null) return holiday.date.loe(to);
        if (to == null) return holiday.date.goe(from);
        return holiday.date.between(from, to);
    }

    private BooleanExpression idLt(Long lastId) {
        return lastId != null ? holiday.id.lt(lastId) : null;
    }

}
