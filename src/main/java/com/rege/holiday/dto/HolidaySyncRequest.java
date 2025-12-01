package com.rege.holiday.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HolidaySyncRequest {

    @Min(1900)
    @Max(2100)
    @NotNull(message = "연도는 필수입니다.")
    private Integer year;

    @NotBlank(message = "국가코드는 필수입니다.")
    @Pattern(regexp = "^[A-Z]{2}$", message = "국가코드는 대문자 알파벳 2글자여야 합니다.")
    private String countryCode;
}
