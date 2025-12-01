package com.rege.holiday.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rege.holiday.contoller.HolidayController;
import com.rege.holiday.dto.HolidaySyncRequest;
import com.rege.holiday.service.HolidayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HolidayController.class)
public class HolidayControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private HolidayService holidayService;

    @Test
    @DisplayName("재동기화 요청 성공")
    void syncHolidays_Success() throws Exception {
        HolidaySyncRequest req = new HolidaySyncRequest(2025, "KR");
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/holidays/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("재동기화 요청 실패 - 국가코드 형식 오류")
    void syncHolidays_ValidationFail_CountryCode() throws Exception {
        HolidaySyncRequest req = new HolidaySyncRequest(2025, "kr");
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/holidays/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("국가코드는 대문자 알파벳 2글자")));
    }

    @Test
    @DisplayName("재동기화 요청 실패 - 필수 파라미터 누락")
    void syncHolidays_ValidationFail_NullParam() throws Exception {
        HolidaySyncRequest req = new HolidaySyncRequest(null, "KR");
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/holidays/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("연도는 필수입니다")));
    }

    @Test
    @DisplayName("삭제 요청 성공")
    void deleteHoliday_Success() throws Exception {
        mockMvc.perform(delete("/api/holidays")
                        .param("year", "2025")
                        .param("countryCode", "KR"))
                .andExpect(status().isNoContent());
    }

}
