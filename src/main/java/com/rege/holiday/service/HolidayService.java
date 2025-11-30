package com.rege.holiday.service;

import com.rege.holiday.api.HolidayApiClient;
import com.rege.holiday.dto.CountryDto;
import com.rege.holiday.dto.HolidayDto;
import com.rege.holiday.entity.Country;
import com.rege.holiday.entity.Holiday;
import com.rege.holiday.mapper.CountryMapper;
import com.rege.holiday.mapper.HolidayMapper;
import com.rege.holiday.repository.CountryRepository;
import com.rege.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService implements ApplicationRunner {

    private final HolidayApiClient holidayApiClient;
    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;
    private final CountryMapper countryMapper;
    private final HolidayMapper holidayMapper;
    private final Executor holidayExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("==========데이터 적제 시작==========");

        long startTime = System.currentTimeMillis();

        List<Country> countries = fetchAndSaveCountry();

        List<Holiday> allHolidays = fetchHolidaysParallel(countries);

        saveHolidays(allHolidays);

        long endTime = System.currentTimeMillis();

        log.info("========== 데이터 적재 완료 (소요시간: {}ms) ==========", (endTime - startTime));
    }

    /**
     * 국가 데이터 조회 및 엔티티 저장
     */
    private List<Country> fetchAndSaveCountry() {
        List<CountryDto> countryDtos = holidayApiClient.getAvailableCountries();

        if (countryDtos == null || countryDtos.isEmpty()) {
            throw new IllegalStateException("국가 목록을 가져오지 못했습니다.");
        }

        List<Country> countries = countryDtos.stream()
                .map(countryMapper::toEntity)
                .toList();

        countryRepository.saveAll(countries);
        log.info("{}개 국가 정보 저장 완료", countries.size());

        return countries;
    }

    /**
     * 국가별 공휴일 병렬 조회
     */
    private List<Holiday> fetchHolidaysParallel(List<Country> countries) {
        List<CompletableFuture<List<Holiday>>> futures = countries.stream()
                .map(country -> CompletableFuture.supplyAsync(
                        () -> fetchHolidaysByCountry(country),
                        holidayExecutor
                ))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }

    /**
     * 특정 국가의 최근 5년 공휴일 단건 조회
     */
    private List<Holiday> fetchHolidaysByCountry(Country country) {
        LocalDate localDate = LocalDate.now();
        int nowYear = localDate.getYear();
        List<Holiday> list = new ArrayList<>();

        for (int year = nowYear - 4; year <= nowYear; year++) {
            try {
                List<HolidayDto> dtos =
                        holidayApiClient.getHolidays(year, country.getCountryCode());

                if (dtos != null) {
                    dtos.forEach(dto -> list.add(holidayMapper.toEntity(dto, country)));
                }

            } catch (Exception e) {
                log.warn("데이터 수집 실패 - 국가: {}, 연도: {}",
                        country.getCountryCode(), year);
            }
        }

        return list;
    }

    /**
     * 공휴일 저장
     */
    private void saveHolidays(List<Holiday> holidays) {
        if (holidays.isEmpty()) {
            log.warn("저장할 공휴일 데이터가 없습니다.");
            return;
        }

        holidayRepository.saveAll(holidays);
        log.info("{}개 공휴일 정보 저장 완료", holidays.size());
    }


}
