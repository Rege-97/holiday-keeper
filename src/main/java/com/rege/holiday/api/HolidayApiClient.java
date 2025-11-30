package com.rege.holiday.api;

import com.rege.holiday.dto.CountryDto;
import com.rege.holiday.dto.HolidayDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidayApiClient {

    private final WebClient webClient;

    // 국가 목록 조회
    public List<CountryDto> getAvailableCountries() {
        return webClient.get()
                .uri("/AvailableCountries")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CountryDto>>() {
                })
                .block();
    }

    // 특정 연도/국가의 공유일 조회
    public List<HolidayDto> getHolidays(int year, String countryCode) {
        return webClient.get()
                .uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<HolidayDto>>() {
                })
                .onErrorResume(e -> {
                    log.warn("공휴일 데이터를 조회하지 못했습니다. 국가: {}, 연도: {}", countryCode, year);
                    return Mono.just(Collections.emptyList());
                })
                .block();
    }
}
