package com.rege.holiday.scheduler;

import com.rege.holiday.api.HolidayApiClient;
import com.rege.holiday.dto.HolidayDto;
import com.rege.holiday.entity.Country;
import com.rege.holiday.entity.Holiday;
import com.rege.holiday.mapper.HolidayMapper;
import com.rege.holiday.repository.CountryRepository;
import com.rege.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidayScheduler {

    private final HolidayApiClient holidayApiClient;
    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;
    private final Executor holidayExecutor;

    @Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
    public void syncHolidays() {
        log.info("========== 정기 데이터 동기화 시작 (전년도/금년도) ==========");
        long start = System.currentTimeMillis();

        int currentYear = LocalDate.now().getYear();
        int[] targetYears = {currentYear - 1, currentYear + 1};

        List<Country> countries = countryRepository.findAll();

        if (countries.isEmpty()) {
            log.warn("동기화할 국가 데이터가 없습니다.");
            return;
        }

        List<CompletableFuture<Void>> futures = countries.stream()
                .map(country -> CompletableFuture.runAsync(() -> {
                    for (int year : targetYears) {
                        try {
                            List<HolidayDto> dtos = holidayApiClient.getHolidays(year, country.getCountryCode());
                            if (dtos != null && !dtos.isEmpty()) {
                                List<Holiday> holidays = dtos.stream()
                                        .map(dto -> holidayMapper.toEntity(dto, country))
                                        .toList();
                                holidayRepository.batchInsert(holidays);
                            }
                        } catch (Exception e) {
                            log.error("배치 동기화 실패 - 국가: {}, 연도: {}", country.getCountryCode(), year, e);
                        }
                    }
                }, holidayExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long end = System.currentTimeMillis();
        log.info("========== 정기 데이터 동기화 완료 (소요시간: {}ms) ==========", (end - start));
    }
}

