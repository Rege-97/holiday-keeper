package com.rege.holiday.service;

import com.rege.holiday.api.HolidayApiClient;
import com.rege.holiday.dto.*;
import com.rege.holiday.entity.Country;
import com.rege.holiday.entity.Holiday;
import com.rege.holiday.exception.NotFoundException;
import com.rege.holiday.mapper.CountryMapper;
import com.rege.holiday.mapper.HolidayMapper;
import com.rege.holiday.repository.CountryRepository;
import com.rege.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${holiday.job.enabled:true}")
    private boolean isJobEnabled;

    /**
     * 앱 시작 시 자동 실행
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isJobEnabled) {
            return;
        }
        loadData();
    }

    /**
     * 최근 5년의 공휴일 수집 및 적재
     */
    public void loadData() {
        log.info("==========데이터 적재 시작==========");
        long startTime = System.currentTimeMillis();

        List<Country> countries = fetchAndSaveCountry();
        fetchAndSaveHolidays(countries);

        long endTime = System.currentTimeMillis();
        log.info("========== 데이터 적재 완료 (소요시간: {}ms) ==========", (endTime - startTime));
    }

    /**
     * 필터 기반 공휴일 조회
     */
    @Transactional(readOnly = true)
    public HolidayPageResponse getHolidays(Integer year, String countryCode, LocalDate from, LocalDate to, String type,
                                           int page, int size, SortOrder sortOrder) {

        int pageIndex = Math.max(page - 1, 0);

        List<Holiday> holidays = holidayRepository.findByFilter(year, countryCode, from, to, type, pageIndex, size,
                sortOrder);

        List<HolidayResponse> responses = holidays.stream()
                .map(holidayMapper::toResponse)
                .toList();

        long totalElements = holidayRepository.countByFilter(year, countryCode, from, to, type);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new HolidayPageResponse(responses, page == 0 ? 1 : page, size, totalElements, totalPages);
    }

    /**
     * 연도와 국가 코드에 따른 공휴일 재동기화
     */
    @Transactional
    public void syncHolidays(HolidaySyncRequest req) {
        Country country = countryRepository.findById(req.getCountryCode())
                .orElseThrow(() -> new NotFoundException("해당 국가를 찾을 수 없습니다."));

        List<HolidayDto> dtos = holidayApiClient.getHolidays(req.getYear(), req.getCountryCode());

        if (dtos == null || dtos.isEmpty()) {
            log.info("갱신할 데이터가 없습니다. (year={}, country={})", req.getYear(), req.getCountryCode());
            return;
        }

        List<Holiday> holidays = dtos.stream()
                .map(dto -> holidayMapper.toEntity(dto, country))
                .toList();

        holidayRepository.batchInsert(holidays);
    }

    /**
     * 연도와 국가 코드에 따른 공휴일 삭제
     */
    @Transactional
    public void deleteHolidays(int year, String countryCode) {
        Country country = countryRepository.findById(countryCode)
                .orElseThrow(() -> new NotFoundException("해당 국가를 찾을 수 없습니다."));

        holidayRepository.deleteByCountryAndYear(countryCode, year);
        log.info("데이터 삭제 완료: year={}, country={}", year, countryCode);
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

        countryRepository.batchInsert(countries);
        log.info("{}개 국가 정보 저장 완료", countries.size());

        return countries;
    }

    /**
     * 국가별 공휴일 병렬 조회 및 저장
     */
    private void fetchAndSaveHolidays(List<Country> countries) {
        List<CompletableFuture<Void>> futures = countries.stream()
                .map(country -> CompletableFuture.runAsync(() -> {
                    try {
                        List<Holiday> holidays = fetchHolidaysByCountry(country);

                        if (!holidays.isEmpty()) {
                            holidayRepository.batchInsert(holidays);
                        }
                    } catch (Exception e) {
                        log.error("국가={} 공휴일 저장 중 오류 발생: {}",
                                country.getCountryCode(), e.getMessage());
                    }
                }, holidayExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * 특정 국가의 최근 5년 공휴일 단건 조회
     */
    private List<Holiday> fetchHolidaysByCountry(Country country) {
        LocalDate localDate = LocalDate.now();
        int nowYear = localDate.getYear();
        List<Holiday> list = new ArrayList<>();

        for (int year = nowYear - 5; year <= nowYear; year++) {
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

}
