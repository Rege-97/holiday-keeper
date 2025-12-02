package com.rege.holiday.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Holiday Keeper API")
                        .description("김채현 플랜잇스퀘어 백엔드 개발자 채용 과제 - 공휴일 관리 서비스")
                        .version("v1.0.0"));
    }
}
