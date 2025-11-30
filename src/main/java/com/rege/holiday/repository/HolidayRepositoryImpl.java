package com.rege.holiday.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rege.holiday.dto.SortOrder;
import com.rege.holiday.entity.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static com.rege.holiday.entity.QCountry.country;
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
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" +
                "ON DUPLICATE KEY UPDATE " +
                "local_name = VALUES(local_name), " +
                "name = VALUES(name), " +
                "country_code = VALUES(country_code), " +
                "fixed = VALUES(fixed), " +
                "global = VALUES(global), " +
                "counties = VALUES(counties), " +
                "launch_year = VALUES(launch_year), " +
                "types = VALUES(types)";

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
                                      int page, int size, SortOrder sortOrder) {
        return queryFactory.selectFrom(holiday)
                .join(holiday.country, country).fetchJoin()
                .where(
                        yearEq(year),
                        countryCodeEq(countryCode),
                        typeContains(type),
                        dateRange(from, to)
                )
                .orderBy(
                        sortBy(sortOrder),
                        holiday.id.desc()
                )
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public Long countByFilter(Integer year, String countryCode, LocalDate from, LocalDate to, String type) {
        return queryFactory.select(holiday.count())
                .from(holiday)
                .where(
                        yearEq(year),
                        countryCodeEq(countryCode),
                        typeContains(type),
                        dateRange(from, to)
                )
                .fetchOne();
    }

    private BooleanExpression yearEq(Integer year) {
        return year != null ? holiday.date.year().eq(year) : null;
    }

    private BooleanExpression countryCodeEq(String countryCode) {
        return countryCode != null ? holiday.country.countryCode.eq(countryCode) : null;
    }

    private BooleanExpression typeContains(String type) {
        if (!StringUtils.hasText(type)) {
            return null;
        }
        return Expressions.stringTemplate("CAST({0} AS string)", holiday.types)
                .contains(type);
    }

    private BooleanExpression dateRange(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;
        if (from == null) return holiday.date.loe(to);
        if (to == null) return holiday.date.goe(from);
        return holiday.date.between(from, to);
    }

    private OrderSpecifier<?> sortBy(SortOrder sortOrder) {
        if (sortOrder == SortOrder.DESC) {
            return holiday.date.desc();
        }
        return holiday.date.asc();
    }

}
