package com.rege.holiday.contoller;

import com.rege.holiday.common.response.ApiResponse;
import com.rege.holiday.dto.HolidayPageResponse;
import com.rege.holiday.dto.HolidaySyncRequest;
import com.rege.holiday.dto.SortOrder;
import com.rege.holiday.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    /**
     * 필터 기반 공휴일 조회
     */
    @GetMapping
    public ResponseEntity<?> getHolidays(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "DESC") SortOrder sortOrder
    ) {
        HolidayPageResponse res = holidayService.getHolidays(year, countryCode, from, to, type, page, size, sortOrder);

        return ResponseEntity.ok(ApiResponse.success(res, "공휴일 조회 성공"));
    }

    /**
     * 연도와 국가 코드에 따른 공휴일 재동기화
     */
    @PostMapping("/sync")
    public ResponseEntity<?> syncHolidays(@RequestBody @Valid HolidaySyncRequest req) {
        holidayService.syncHolidays(req);
        return ResponseEntity.noContent().build();
    }

    /**
     * 연도와 국가 코드에 따른 공휴일 삭제
     */
    @DeleteMapping
    public ResponseEntity<?> deleteHoliday(@RequestParam(required = false) Integer year,
                                           @RequestParam(required = false) String countryCode) {
        holidayService.deleteHolidays(year, countryCode);
        return ResponseEntity.noContent().build();
    }
}
