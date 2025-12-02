package com.rege.holiday.contoller;

import com.rege.holiday.common.response.ApiResponse;
import com.rege.holiday.dto.HolidayPageResponse;
import com.rege.holiday.dto.HolidaySyncRequest;
import com.rege.holiday.dto.SortOrder;
import com.rege.holiday.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Holiday API", description = "공휴일 조회, 동기화, 삭제 관리")
@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    /**
     * 필터 기반 공휴일 조회
     */
    @Operation(summary = "공휴일 검색 및 조회",
            description = "연도, 국가, 기간, 타입 등 다양한 조건으로 공휴일을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<HolidayPageResponse>> getHolidays(
            @Parameter(description = "연도 (예: 2025)") @RequestParam(required = false) Integer year,
            @Parameter(description = "국가 코드 (예: KR)") @RequestParam(required = false) String countryCode,
            @Parameter(description = "시작일 (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso =
                    DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "종료일 (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso =
                    DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "공휴일 타입 (예: Public)") @RequestParam(required = false) String type,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 순서 (ASC/DESC)") @RequestParam(defaultValue = "DESC") SortOrder sortOrder
    ) {
        HolidayPageResponse res = holidayService.getHolidays(year, countryCode, from, to, type, page, size, sortOrder);

        return ResponseEntity.ok(ApiResponse.success(res, "공휴일 조회 성공"));
    }

    /**
     * 연도와 국가 코드에 따른 공휴일 재동기화
     */
    @Operation(summary = "공휴일 데이터 재동기화",
            description = "특정 연도와 국가의 데이터를 외부 API에서 다시 가져와 갱신합니다.")
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<Void>> syncHolidays(@RequestBody @Valid HolidaySyncRequest req) {
        holidayService.syncHolidays(req);
        return ResponseEntity.noContent().build();
    }

    /**
     * 연도와 국가 코드에 따른 공휴일 삭제
     */
    @Operation(summary = "공휴일 데이터 삭제",
            description = "특정 연도와 국가의 공휴일 데이터를 DB에서 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteHoliday(
            @Parameter(description = "연도 (예: 2025)") @RequestParam Integer year,
            @Parameter(description = "국가 코드 (예: KR)") @RequestParam String countryCode) {
        holidayService.deleteHolidays(year, countryCode);
        return ResponseEntity.noContent().build();
    }
}
