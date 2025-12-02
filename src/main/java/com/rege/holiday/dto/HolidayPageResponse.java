package com.rege.holiday.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "공휴일 조회 결과 (페이징 정보 포함)")
public class HolidayPageResponse {

    @Schema(description = "조회된 공휴일 목록")
    private List<HolidayResponse> holidays;

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private int page;

    @Schema(description = "페이지 당 데이터 수", example = "20")
    private int size;

    @Schema(description = "총 데이터 개수", example = "125")
    private long totalElements;

    @Schema(description = "총 페이지 수", example = "7")
    private int totalPages;
}