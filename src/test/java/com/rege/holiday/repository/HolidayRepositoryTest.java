package com.rege.holiday.repository;

import com.rege.holiday.config.QueryDslConfig;
import com.rege.holiday.dto.SortOrder;
import com.rege.holiday.entity.Country;
import com.rege.holiday.entity.Holiday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslConfig.class, HolidayRepositoryImpl.class})
public class HolidayRepositoryTest {

    @Autowired
    private HolidayRepository holidayRepository;
    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    void setUp() {
        Country kr = Country.builder()
                .countryCode("KR")
                .name("South Korea")
                .build();
        countryRepository.save(kr);

        Holiday h1 = Holiday.builder()
                .date(LocalDate.of(2025, 1, 1))
                .localName("신정")
                .name("New Year's Day")
                .country(kr)
                .types(List.of("Public"))
                .build();

        Holiday h2 = Holiday.builder()
                .date(LocalDate.of(2025, 12, 25))
                .localName("크리스마스")
                .name("Christmas Day")
                .country(kr)
                .types(List.of("Public", "Religious"))
                .build();

        holidayRepository.saveAll(List.of(h1, h2));
    }

    @Test
    @DisplayName("연도와 국가코드로 공휴일을 조회한다.")
    void findByFilter_YearAndCountry() {
        List<Holiday> result = holidayRepository.findByFilter(
                2025, "KR", null, null, null, 0, 10, SortOrder.ASC
        );
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("New Year's Day");
    }

    @Test
    @DisplayName("특정 기간(from~to)으로 공휴일을 조회한다.")
    void findByFilter_DateRange() {
        List<Holiday> result = holidayRepository.findByFilter(
                null, "KR",
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 31),
                null, 0, 10, SortOrder.ASC
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocalName()).isEqualTo("크리스마스");
    }

    @Test
    @DisplayName("공휴일 타입(types)으로 검색한다.")
    void findByFilter_Type() {
        List<Holiday> result = holidayRepository.findByFilter(
                2025, "KR", null, null, "Religious", 0, 10, SortOrder.ASC
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Christmas Day");
    }

    @Test
    @DisplayName("조건에 맞는 데이터가 없으면 빈 리스트를 반환한다.")
    void findByFilter_NoResult() {
        List<Holiday> result = holidayRepository.findByFilter(
                2099, "KR", null, null, null, 0, 10, SortOrder.ASC
        );

        assertThat(result).isEmpty();
    }
}
