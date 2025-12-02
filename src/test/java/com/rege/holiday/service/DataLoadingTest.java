package com.rege.holiday.service;

import com.rege.holiday.api.HolidayApiClient;
import com.rege.holiday.dto.CountryDto;
import com.rege.holiday.dto.HolidayDto;
import com.rege.holiday.repository.CountryRepository;
import com.rege.holiday.repository.HolidayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest(properties = "holiday.job.enabled=false")
public class DataLoadingTest {

    @Autowired
    private HolidayService holidayService;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private HolidayRepository holidayRepository;
    @MockitoBean
    private HolidayApiClient holidayApiClient;

    @Test
    @DisplayName("앱 실행 시 외부 API 데이터를 가져와 DB에 적재한다.")
    void dataLoadingIntegrationTest() throws Exception {
        // given
        CountryDto korea = CountryDto.builder()
                .countryCode("KR")
                .name("South Korea")
                .build();
        given(holidayApiClient.getAvailableCountries()).willReturn(List.of(korea));

        List<HolidayDto> holidays = List.of(
                HolidayDto.builder()
                        .date("2020-01-01")
                        .localName("신정")
                        .name("New Year's Day")
                        .countryCode("KR")
                        .types(List.of("Public"))
                        .build(),

                HolidayDto.builder()
                        .date("2021-01-01")
                        .localName("신정")
                        .name("New Year's Day")
                        .countryCode("KR")
                        .types(List.of("Public"))
                        .build(),

                HolidayDto.builder()
                        .date("2022-01-01")
                        .localName("신정")
                        .name("New Year's Day")
                        .countryCode("KR")
                        .types(List.of("Public"))
                        .build(),

                HolidayDto.builder()
                        .date("2023-01-01")
                        .localName("신정")
                        .name("New Year's Day")
                        .countryCode("KR")
                        .types(List.of("Public"))
                        .build(),

                HolidayDto.builder()
                        .date("2024-01-01")
                        .localName("신정")
                        .name("New Year's Day")
                        .countryCode("KR")
                        .types(List.of("Public"))
                        .build(),

                HolidayDto.builder()
                        .date("2025-01-01")
                        .localName("신정")
                        .name("New Year's Day")
                        .countryCode("KR")
                        .types(List.of("Public"))
                        .build()
        );
        given(holidayApiClient.getHolidays(anyInt(), anyString())).willReturn(holidays);

        // when
        holidayService.loadData();

        // then
        assertThat(countryRepository.count()).isEqualTo(1);
        assertThat(holidayRepository.count()).isEqualTo(6);
    }

    @Test
    @DisplayName("국가 목록 조회 실패 시 예외가 발생하여 데이터 적재가 중단된다.")
    void run_AvailableCountries_Fail() {
        // given
        given(holidayApiClient.getAvailableCountries()).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> holidayService.loadData())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("국가 목록을 가져오지 못했습니다.");
    }

    @Test
    @DisplayName("특정 국가 공휴일 조회 실패 시, 해당 건만 건너뛰고 실행 완료된다.")
    void run_GetHolidays_Fail() throws Exception {
        // given
        CountryDto korea = CountryDto.builder()
                .countryCode("KR")
                .name("South Korea")
                .build();
        given(holidayApiClient.getAvailableCountries()).willReturn(List.of(korea));

        // 공휴일 API가 에러 발생 가정
        given(holidayApiClient.getHolidays(anyInt(), anyString()))
                .willThrow(new RuntimeException("API Error"));

        // when
        holidayService.loadData();

        // then
        assertThat(countryRepository.count()).isEqualTo(1);
        assertThat(holidayRepository.count()).isEqualTo(0);
    }
}
