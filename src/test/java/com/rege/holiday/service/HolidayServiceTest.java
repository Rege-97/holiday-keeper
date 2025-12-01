package com.rege.holiday.service;

import com.rege.holiday.api.HolidayApiClient;
import com.rege.holiday.dto.HolidayDto;
import com.rege.holiday.dto.HolidaySyncRequest;
import com.rege.holiday.entity.Country;
import com.rege.holiday.exception.NotFoundException;
import com.rege.holiday.mapper.CountryMapper;
import com.rege.holiday.mapper.HolidayMapper;
import com.rege.holiday.repository.CountryRepository;
import com.rege.holiday.repository.HolidayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {

    @InjectMocks
    private HolidayService holidayService;
    @Mock
    private HolidayApiClient holidayApiClient;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private HolidayRepository holidayRepository;
    @Mock
    private HolidayMapper holidayMapper;
    @Mock
    private CountryMapper countryMapper;

    @Test
    @DisplayName("재동기화 요청 시 외부 API를 호출하고 DB에 저장한다.")
    void syncHolidays_Success() {
        // given
        HolidaySyncRequest req = new HolidaySyncRequest(2025, "KR");

        Country mockCountry = Country.builder()
                .countryCode("KR")
                .build();

        given(countryRepository.findById("KR"))
                .willReturn(Optional.of(mockCountry));

        HolidayDto mockDto = new HolidayDto();
        given(holidayApiClient.getHolidays(2025, "KR"))
                .willReturn(List.of(mockDto));

        // when
        holidayService.syncHolidays(req);

        // then
        verify(holidayRepository, times(1))
                .batchInsert(anyList());
    }

    @Test
    @DisplayName("존재하지 않는 국가 재동기화 시 NotFoundException이 발생한다. (실패)")
    void syncHolidays_NotFound_Fail() {
        // given
        HolidaySyncRequest req = new HolidaySyncRequest(2025, "KR");
        given(countryRepository.findById(req.getCountryCode()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> holidayService.syncHolidays(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("해당 국가를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("데이터 삭제 시 Repository의 삭제 메서드가 호출된다.")
    void deleteHolidays_Success() {
        // given
        String countryCode = "KR";
        int year = 2025;
        given(countryRepository.findById(countryCode))
                .willReturn(Optional.of(Country.builder().build()));

        // when
        holidayService.deleteHolidays(year, countryCode);

        // then
        verify(holidayRepository, times(1))
                .deleteByCountryAndYear(countryCode, year);
    }

    @Test
    @DisplayName("존재하지 않는 국가 삭제 시 NotFoundException이 발생한다. (실패)")
    void deleteHolidays_NotFound_Fail() {
        // given
        String invalidCode = "XX";
        int year = 2025;
        given(countryRepository.findById(invalidCode))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> holidayService.deleteHolidays(year, invalidCode))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("해당 국가를 찾을 수 없습니다");
    }
}
