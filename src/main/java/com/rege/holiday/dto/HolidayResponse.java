package com.rege.holiday.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Schema(description = "공휴일 상세 정보 응답")
public class HolidayResponse {

    @Schema(description = "공휴일 ID", example = "1")
    private Long id;

    @Schema(description = "날짜", example = "2025-12-25")
    private LocalDate date;

    @Schema(description = "현지 공휴일명", example = "크리스마스")
    private String localName;

    @Schema(description = "영문 공휴일명", example = "Christmas Day")
    private String name;

    @Schema(description = "국가명", example = "South Korea")
    private String countryName;

    @Schema(description = "고정 공휴일 여부", example = "false")
    private boolean fixed;

    @Schema(description = "전역 공휴일 여부", example = "true")
    private boolean global;

    @Schema(description = "적용 지역 목록 (전역인 경우 null)", example = "[\"US-AL\", \"US-GA\"]")
    private List<String> counties;

    @Schema(description = "시작 연도", example = "null")
    private Integer launchYear;

    @Schema(description = "공휴일 타입 목록", example = "[\"Public\"]")
    private List<String> types;
}
