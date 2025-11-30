package com.rege.holiday.service;

import com.rege.holiday.api.HolidayApiClient;
import com.rege.holiday.dto.CountryDto;
import com.rege.holiday.entity.Country;
import com.rege.holiday.mapper.CountryMapper;
import com.rege.holiday.repository.CountryRepository;
import com.rege.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService implements ApplicationRunner {

    private final HolidayApiClient holidayApiClient;
    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;
    private final CountryMapper countryMapper;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("==========데이터 적제 시작==========");
        long startTime = System.currentTimeMillis();

        List<CountryDto> countryDtos = holidayApiClient.getAvailableCountries();
        if (countryDtos == null || countryDtos.isEmpty()) {
            log.error("국가 목록을 가져오지 못했습니다.");
            return;
        }

        List<Country> countries = countryDtos.stream()
                .map(countryMapper::toEntity)
                .toList();
        countryRepository.saveAll(countries);
        log.info("{}개 국가 정보 저장 완료", countryDtos.size());


        long endTime = System.currentTimeMillis();
        log.info("========== 데이터 적재 완료 (소요시간: {}ms) ==========", (endTime - startTime));
    }

}
