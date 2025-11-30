package com.rege.holiday.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private static final String BASE_URL = "https://date.nager.at/api/v3";

    @Bean
    public WebClient webClient(ObjectMapper objectMapper) {

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(
                            objectMapper,
                            MediaType.APPLICATION_JSON,
                            new MediaType("text", "json")
                    );

                    Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(
                            objectMapper,
                            MediaType.APPLICATION_JSON,
                            new MediaType("text", "json")
                    );

                    configurer.defaultCodecs().jackson2JsonDecoder(decoder);
                    configurer.defaultCodecs().jackson2JsonEncoder(encoder);
                })
                .build();

        return WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(strategies)
                .build();
    }
}
