package com.rege.holiday.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공휴일 재동기화 요청")
public class HolidaySyncRequest {

    @Schema(description = "대상 연도", example = "2025")
    @Min(1900)
    @Max(2100)
    @NotNull(message = "연도는 필수입니다.")
    private Integer year;

    @Schema(description = "국가 코드", example = "KR")
    @NotBlank(message = "국가코드는 필수입니다.")
    @Pattern(regexp = "^[A-Z]{2}$", message = "국가코드는 대문자 알파벳 2글자여야 합니다.")
    private String countryCode;
}
